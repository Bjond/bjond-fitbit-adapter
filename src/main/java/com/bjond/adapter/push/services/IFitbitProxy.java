package com.bjond.adapter.push.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface IFitbitProxy {
	
	@POST
	@Path("/axisWebServices/api/dataIntake/takeAdmissionOrDischarge")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	String updateHL7Data(@HeaderParam("Authorization") final String authentication, final String data);

}
