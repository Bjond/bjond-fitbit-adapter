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

package com.bjond.adapter.push.proxy;


import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/** <p> RestEasy Proxy Server for the Bjond Adapter REST API</p>

 Example of usage: https://docs.jboss.org/resteasy/docs/3.0-beta-3/userguide/html/RESTEasy_Client_Framework.html

  Client client = ClientFactory.newClient();
  WebTarget target = client.target("localhost:8080/server-core/services/assessmentservice/");
  ResteasyWebTarget rtarget = (ResteasyWebTarget)target;
  
  IBjondServer simple = rtarget.proxy(IBjondServer.class);
  IBjondServer.push("eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.E8RGUsRCKLv7g1zfMOkVAnp-X7KpHoqBRxxhbpZZ4b6aIjBpD-m78A.lJhUyzJXqxvAiP3jP93Yng.gamWPOrmGqrlui5_2rTeSQ.XM5fouuMlSMnhiGTi0c6kA","hello world");

  Simple as that.        

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */


public interface IBjondIntegrationManager {

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/server-core/services/integrationmanager/register") 
    Response register(final String jsonService) throws Exception;

}
