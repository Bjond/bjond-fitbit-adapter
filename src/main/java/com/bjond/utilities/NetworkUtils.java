/*  Copyright (c) 2014
 *  by Bjönd, Inc., Boston, MA
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

package com.bjond.utilities;


import java.io.IOException;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bjond.constants.ErrorCodes;
// Google Guave
import com.google.common.collect.ImmutableMap;
// Google 

import lombok.val;



/** <p> Network utilties which obviously includes REST convenience
    methods.  </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Cräsh' Agneta</a>
 *
 */

public class NetworkUtils {

	public static Map<String, String> generateErrorMap(String errorMessage){
	    if(null == errorMessage) { errorMessage = "No error message provided.";}
	
	    return ImmutableMap.of("error", errorMessage);
	}

	public static Map<String, String> generateSuccessMap(@NotNull(message="message must not be null.") final String message){
	    return ImmutableMap.of("status", message);
	}

	/**
	 * This override allows us to specify an id. This is useful in a create case where
	 * the client does not yet have a GUID for the object it is creating.
	 * 
	 * @param message The message.
	 * @param id ID of the newly created object.
	 * @return The data to send back to the client.
	 */
	public static Map<String, String> generateSuccessMap(@NotNull(message="message must not be null.") String message,  @NotNull(message="id must not be null.") String id){
	    return ImmutableMap.of("status", message, "id", id);
	}

	/**
	 *  <code>errorResponse</code> method accepts an errorString and a codeString and construct the 
	 *  HTTP response object.
	 *
	 * @param status a <code>Response.Status</code> value
	 * @param errorString a <code>String</code> value
	 * @param code an <code>ERROR_CODES</code> value
	 * @return a <code>Response</code> value
	 */
	
	public static Response errorResponse(final Response.Status status, final String errorString, final ErrorCodes.BJOND_HTTP_ERROR_CODES code) {
	    return Response.status(status).entity(ImmutableMap.of("error", errorString, "code", code.name())).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	public static Response errorResponse(final Response.Status status, final String errorString, final String nativeString, final ErrorCodes.BJOND_HTTP_ERROR_CODES code) {
	    return Response.status(status).entity(ImmutableMap.of("error", errorString, "code", code.name(), "nativeerror", nativeString)).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * safeExtract will return type T if the response contains T or a JSON String representation of T.
     * NULL values are not handled and will toss a RuntimeException. Be warned. Not goof proof.
	 *
	 * @param response
	 * @param c
	 * @return
	 *
	 * @throws IOException
	 */
    @SuppressWarnings("unchecked")
    public static <T> T safeExtract(final Response response, final Class<T> c) throws IOException {
        val obj = response.getEntity();
        return (obj instanceof String) ? JSONUtils.fromJSON((String)obj,c) : (T)response.getEntity();
    }

}
