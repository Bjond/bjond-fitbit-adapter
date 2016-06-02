/*  Copyright (c) 2016
 *  by Bjönd Health, Inc., Boston, MA
 *
 *  This software is furnished under a license and may be used only in
 *  accordance with the terms of such license.  This software may not be
 *  provided or otherwise made available to any other party.  No title to
 *  nor ownership of the software is hereby transferred.
 *
 *  This software is the intellectual property of Bjönd Health, Inc.,
 *  and is protected by the copyright laws of the United States of America.
 *  All rights reserved internationally.
 *
 */

package com.bjond.adapter.push.services;


import static javax.ejb.TransactionAttributeType.REQUIRED;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jose4j.base64url.Base64;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import com.bjond.adapter.definition.FitbitAdapter;
import com.bjond.adapter.definition.FitbitDefinition;
import com.bjond.adapter.push.proxy.IBjondIntegrationManager;
import com.bjond.constants.ErrorCodes;
import com.bjond.persistence.bjondservice.BjondService;
import com.bjond.persistence.bjondservice.GroupConfiguration;
import com.bjond.security.cryptography.JWTUtil;
import com.bjond.utilities.JSONUtils;
import com.bjond.utilities.NetworkUtils;
import com.google.common.collect.ImmutableMap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;


/** <p> The Slack Service that provides connectivity to this adapter via a REST interface </p>

http://localhost:8080/bjond-slack-adapter/services/adapter/registerwithbjond

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Cräsh' Agneta</a>
 *
 */


@Path("/adapter")
@Stateless
@Slf4j
@SuppressFBWarnings({"EI_EXPOSE_REP","EI_EXPOSE_REP2"})
public class FitbitAdapterService {

    /////////////////////////////////////////////////////////////////////////
    //          environment variables required for proper operation.       //
    /////////////////////////////////////////////////////////////////////////
    private static final String CLAIM_KEY                   = "json";
    final private static String ISSUER                      = "Bjönd, Inc.";
    final private static String BJOND_ADAPTER_SUBJECT       = System.getenv("FITBIT_BJOND_ADAPTER_SUBJECT");
    final private static String BJOND_ADAPTER_AUDIENCE      = System.getenv("FITBIT_BJOND_ADAPTER_AUDIENCE");
    final private static String BJOND_SERVER                = System.getenv("FITBIT_BJOND_SERVER");
    final private static String BJOND_SERVER_ENCRYPTION_KEY = System.getenv("FITBIT_BJOND_SERVER_ENCRYPTION_KEY");
    final private static String UUID_REGEX                  = "^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$";
    
    @PersistenceContext(unitName = "bjondfitbitadapter", type = PersistenceContextType.TRANSACTION)
    @Getter private EntityManager entityManager;

    // Demonstrates that CDI is indeed working.
    @Resource
    private SessionContext sc;

    // The decoded AES key for the JWT as obtained from BJOND_SERVER_ENCRYPTION_KEY.
    // Null if no key is present.
    @Getter @Setter private byte[] AESKeyDecoded;
    
    @PostConstruct
    protected void startService()  {
        log.info("FitbitAdapterService has started...");
        
        if(StringUtils.isBlank(BJOND_SERVER)) {
            log.warn("The BJOND_SERVER environment variable is not set!");
            log.warn("Communication with the Bjond Health server will be impossible.");
        }

        if(StringUtils.isBlank(BJOND_SERVER_ENCRYPTION_KEY)) {
            log.warn("The BJOND_SERVER_ENCRYPTION_KEY environment variable is not set!");
            log.warn("Communication with the Bjond Health server beyond simple registration will not be possible.");
        } else {
            setAESKeyDecoded(JWTUtil.base64Decode(BJOND_SERVER_ENCRYPTION_KEY));
        }

        if(StringUtils.isBlank(BJOND_ADAPTER_SUBJECT)) {
            log.warn("The BJOND_ADAPTER_SUBJECT environment variable is not set!");
            log.warn("Communication with the Bjond Health server beyond simple registration will not be possible.");
        }
        
        if(StringUtils.isBlank(BJOND_ADAPTER_AUDIENCE)) {
            log.warn("The BJOND_ADAPTER_AUDIENCE environment variable is not set!");
            log.warn("Communication with the Bjond Health server beyond simple registration will not be possible.");
        }

    }

    @GET
    @Path("/registerwithbjond")
    public Response registerwithbjond(@QueryParam("server") final String server) throws Exception {
    	
    	FitbitAdapter adapterDefinition = FitbitDefinition.getDefinition();

        final Client client = ClientBuilder.newClient();
        WebTarget target = null;
        if(server == null) {
        	target = client.target(BJOND_SERVER);
        } else {
        	target = client.target(server);
        }
        final ResteasyWebTarget rtarget = (ResteasyWebTarget) target;
        
        final IBjondIntegrationManager simple = rtarget.proxy(IBjondIntegrationManager.class);
        final Response response = simple.register(JSONUtils.toJSON(adapterDefinition));

        if (response.getStatus() != Status.OK.getStatusCode()) {
            final String errorString = "Adapter reached. However, could not reach the Bjond server... Received server status of " + Integer.toString(response.getStatus());
            return NetworkUtils.errorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                                              errorString,
                                              ErrorCodes.BJOND_HTTP_ERROR_CODES.BJOND_SERVER_COMMUNICATION_FAILURE);
        }

        return Response.ok(response.readEntity(String.class)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @POST
    @Path("/{groupid}/configure")
    @TransactionAttribute(REQUIRED)
    public Response configureServiceGroup(@Pattern(regexp = UUID_REGEX, message="UUID invalid format" )
                                          @PathParam("groupid")
                                          @NotNull(message="groupid must not be null") final String groupid,
                                          @NotNull(message="token must not be null") String token) throws Exception {
        
        log.info("Adapter Service - configureServiceGroup method has been called");
        log.info("GroupID - " + groupid);
        log.info("Token - " + token);
        val configJson = extractJSONClaimIfPresent(validateAndExtractJWTToken(token));
        if (configJson != null && !configJson.equals("")) {
            final GroupConfiguration parsed = JSONUtils.fromJSON(configJson, GroupConfiguration.class);
            entityManager.merge(parsed);
        } else {
            throw new Exception("Bjond-Axis-Adapter:configureServiceGroup - The configurationJson received was empty. Failed to update service.");
        }
        return Response.ok("Ok").type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/{groupid}/read")
    public Response getGroupConfiguration(@Pattern(regexp = UUID_REGEX, message="UUID invalid format" )
                                          @PathParam("groupid")
                                          @NotNull(message="groupid must not be null") final String groupid) throws IOException {
        log.info("Adapter Service - getGroupConfiguration method has been called");
        log.info("GroupID - " + groupid);

        final GroupConfiguration result = GroupConfiguration.getByGroupId (entityManager, groupid);
        return (result != null) ? generateJWTTokenAsResponse(JSONUtils.toJSON(result)) : generateJWTTokenAsResponse("");
    }


    @GET
    @Path("/schema")
    public Response getServiceSchema() throws Exception {
        log.info("Adapter Service - SCHEMA method has been called");
        final String schema = JSONUtils.getJsonSchema(GroupConfiguration.class);
        return generateJWTTokenAsResponse(schema);
    }



    @POST
    @Path("/{groupid}/register")
    @TransactionAttribute(REQUIRED)
    public Response registerGroupEndpoint(@Pattern(regexp = UUID_REGEX, message="UUID invalid format" )
                                          @PathParam("groupid")
                                          @NotNull(message="groupid must not be null") final String groupid,
                                          @NotNull(message="endpoint must not be null") @QueryParam("endpoint") final String endpoint) throws Exception {
        log.info("Adapter Service - registerGroupEndpoint method has been called");
        log.info("GroupID - " + groupid);
        log.info("Endpoint - " + endpoint);

        final BjondService service = BjondService.findOrMakeNewByGroupId(entityManager, groupid);
        service.setEndpoint(endpoint);

        entityManager.persist(service);

        return Response.ok().entity(service).type(MediaType.APPLICATION_JSON_TYPE).build();
    }


    /**
     * Fires event with payload.
     * 
     * @param groupid
     * @return
     * @throws Exception
     */
    @POST
    @Path("/fireeventwithpayload")
    public Response fireEventwithpayload (@Pattern(regexp = UUID_REGEX, message="UUID invalid format")
                               @NotNull(message="groupid must not be null")
                               @QueryParam("groupid") final String groupid,
                               @Pattern(regexp = UUID_REGEX, message="UUID invalid format")
                               @NotNull(message="eventid must not be null")
                               @QueryParam("eventid") final String eventid,
                               @NotNull(message="payload must not be null")
                               final String payload) throws Exception {
        return fireEvent(eventid, groupid, JSONUtils.toMap(payload));
    }


    /**
     * Hit the event endpoint. Make sure you provide the id of the event that is being fired.
     * 
     * @param eventID The id of the event that is being fired.
     * @param groupid The group it is being fired to.
     * @param payload The key/value pairs of fields.
     * @return
     * @throws Exception
     */
    private Response fireEvent(final String eventID, final String groupid, Map<String, Object> payload) throws Exception {
    	final BjondService service = BjondService.findFirstByGroupId(entityManager, groupid);
    	if (service == null || service.getEndpoint() == null) {
            final String errorString = "Could not find an event endpoint for the given group id.";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", errorString)).type(MediaType.APPLICATION_JSON_TYPE).build();
        } else {
            final String endpointIncludingEvent = service.getEndpoint() + "/" + eventID;
        	final ResteasyClient client    = new ResteasyClientBuilder().build();
            final ResteasyWebTarget target = client.target(endpointIncludingEvent);
            final String json              = JSONUtils.toJSON(payload);

            final String token = generateJWTToken(json);

            log.info("Sending POST request to " + endpointIncludingEvent);
            final Response response = target.request().post(Entity.entity(token, MediaType.TEXT_PLAIN));
            log.info(Integer.toString(response.getStatus()));
            if (response.getStatus() == Status.OK.getStatusCode()) {
                return Response.status(Response.Status.OK).entity(NetworkUtils.generateSuccessMap("ok")).type(MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return NetworkUtils.errorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                                                  "There was an error connecting to Bjond server-core.",
                                                  ErrorCodes.BJOND_HTTP_ERROR_CODES.BJOND_SERVER_COMMUNICATION_FAILURE);
            }
        }
    }

    /**
	 *  Given a JWT claim will extract the 'json' if available.
	 *  If claim is not available the empty string "" is returned.
     *
	 * @param claims
	 * @return
	 */
    private String extractJSONClaimIfPresent(final JwtClaims claims) {
        // We are valid and have claims. There is a JSON structure in the claims
        final Map<String, List<Object>> claimsMap = claims.flattenClaims();

        // Check if claim exists: optional.
        if(claimsMap.isEmpty() ||
           !claimsMap.containsKey(CLAIM_KEY) ||
           claimsMap.get(CLAIM_KEY).isEmpty()) {
            return "";
        }

        return claimsMap.get(CLAIM_KEY).get(0).toString();
    }
    
    /**
	 * Given a JWT token will attempt to validate it.
     * If successful the claims will be returned otherwise an exception is tossed.
	 * 
	 * @param token
     * @return
	 * 
	 * @throws Exception
	 */
    private JwtClaims validateAndExtractJWTToken(final String token) throws Exception {
        if(getAESKeyDecoded() == null) {
            throw new Exception("NO JWT Key set in the environment");
        }

        final Key key = JWTUtil.generateAESKey(getAESKeyDecoded());
        return JWTUtil.validateTokenAndProcessClaims(key,
                                                     ISSUER,
                                                     BJOND_ADAPTER_AUDIENCE,
                                                     BJOND_ADAPTER_SUBJECT,
                                                     10,
                                                     token);
    }
    

	/**
	 *  Given a JSON string, will generate and proper JWT Token
     *  and will embed the token in a Response.
     *
     *  Internal Errors that are detected are also returned within a response.
	 * 
	 * @param json
	 * @return
	 */
	private Response generateJWTTokenAsResponse(final String json) {
        try {
            // Check if a key exists!
            if(getAESKeyDecoded() == null) {
                log.error("NO JWT Key set in the environment.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "NO JWT Key set in the environment")).type(MediaType.TEXT_PLAIN).build();            
            }

            final String token = generateJWTToken(json);
            return Response.ok(token).build();
        } catch(final JoseException  e) {
            log.error("Unexpected Exceptoin during Jose JWT token generation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", e.getMessage())).type(MediaType.TEXT_PLAIN).build();            
        }
	}


	/**
     * Generates a JWT Token with the json embedded within a claim.
     * 
	 * @param json
	 * @return
	 * @throws JoseException
	 */
	private String generateJWTToken(final String json) throws JoseException {
        // Check if a key exists!
        if(getAESKeyDecoded() == null) {
            log.error("NO JWT Key set in the environment.");
            throw new JoseException("NO JWT Key set in the environment");
        }


        final Key key = JWTUtil.generateAESKey(getAESKeyDecoded());
		final Map<String, List<String>> claimsMap = new HashMap<>();

		claimsMap.put("json", Arrays.asList(json));
        return JWTUtil.generateJWT_AES128(
                                          key,
                                          ISSUER,
                                          BJOND_ADAPTER_AUDIENCE,
                                          BJOND_ADAPTER_SUBJECT,
                                          claimsMap,
                                          1
                                          );
	}
}
