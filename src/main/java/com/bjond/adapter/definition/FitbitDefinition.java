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

package com.bjond.adapter.definition;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FitbitDefinition {
	
    final private static String ADAPTER_PUBLIC_URL = System.getenv("FITBIT_ADAPTER_PUBLIC_URL");
    final public static String ADAPTER_ID = "3929e8fb-9bb3-4eca-8d50-d3490be3bbe8";
    final public static String SLASH_EVENT_ID = "6ff2f4e3-b306-40f1-8fa9-5f8ee1bdb80d";
	
	public static FitbitAdapter getDefinition() {
		FitbitAdapter adapter = new FitbitAdapter();
		adapter.setAuthor("Bjönd, Inc");
		adapter.setId(ADAPTER_ID);
		adapter.setName("Fitbit");
		adapter.setRootEndpoint(StringUtils.defaultIfBlank(ADAPTER_PUBLIC_URL, "http://localhost:8080") + "/bjond-fitbit-adapter/services/adapter");
		adapter.setConfigURL(StringUtils.defaultIfBlank(ADAPTER_PUBLIC_URL, "http://localhost:8080") + "/bjond-fitbit-adapter/services/adapter");
		adapter.setIconURL("http://www.elissadebruyn.com/port/Plans/FB/Fitbit-Logo.png");
		adapter.setDescription("Enables you to create rules on fitbit events.");
		adapter.setIntegrationEvent(getEvents(adapter.getId()));
		adapter.setIntegrationConsequence(getConsequences(adapter.getId()));
		return adapter;
	}
	
	// Note- these are from the Slack adapter for now until we know how to integrate with FitBit API. -BCF
	private static Set<FitbitEvent> getEvents(String serviceid) {
		Set<FitbitEvent> events = new HashSet<FitbitEvent>();
		FitbitEvent slashEvent = new FitbitEvent();
		slashEvent.setId(SLASH_EVENT_ID);
		slashEvent.setJsonKey("slash");
		slashEvent.setName("Slash Command");
		slashEvent.setDescription("This reacts to a slash command from the slack general channel.");
		slashEvent.setFields(getFields(slashEvent.getJsonKey(), slashEvent.getId()));
		slashEvent.setServiceid(serviceid);
		events.add(slashEvent);
		return events;
	}
	
	// Note- these are from the Slack adapter for now until we know how to integrate with FitBit API. -BCF
	private static Set<FitbitField> getFields(String event, String eventid) {
		Set<FitbitField> fields = new HashSet<FitbitField>();
		switch (event){
        case "slash":
        	FitbitField userField = new FitbitField();
        	userField.setId("c5e3ae57-813a-431c-b5ec-218f7d730514");
        	userField.setFieldType("Person");
        	userField.setJsonKey("person");
        	userField.setName("Person");
        	userField.setDescription("The person who issued the command.");
        	userField.setEvent(eventid);
        	fields.add(userField);
        	
        	FitbitField command = new FitbitField();
        	command.setId("842a9c12-8ddd-4fed-8fdd-07cfe6041baa");
        	command.setFieldType("String");
        	command.setJsonKey("command");
        	command.setName("Command");
        	command.setDescription("The command portion of the slash command.");
        	command.setEvent(eventid);
        	fields.add(command);
        	break;
        default:
            log.warn("Ignoring unknown event {}.", event);
            break;
		}
		
		return fields;
	}
	
	// Note- these are from the Slack adapter for now until we know how to integrate with FitBit API. -BCF
	private static Set<FitbitConsequence> getConsequences(String adapterid) {
		Set<FitbitConsequence> consequences = new HashSet<>();
		FitbitConsequence directSlackNotification = new FitbitConsequence();
		directSlackNotification.setId("7c16f714-deed-4863-8789-ddeaf12f32d6");
		directSlackNotification.setDescription("Sends a notification directly to the slack user.");
		directSlackNotification.setJsonKey("slack-direct-notification");
		directSlackNotification.setName("Slack Direct Notification");
		directSlackNotification.setServiceid(adapterid);
		directSlackNotification.setWebhook("/consequence/direct");
		
		FitbitConsequence publicSlackNotification = new FitbitConsequence();
		publicSlackNotification.setId("1bc3c138-e312-4b3b-ba55-393446d82f43");
		publicSlackNotification.setDescription("This will send a notification to the Slack channel configured to use with Bjönd. Be careful of privacy when using this.");
		publicSlackNotification.setJsonKey("general_notification");
		publicSlackNotification.setName("Slack Public Notification");
		publicSlackNotification.setServiceid(adapterid);
		publicSlackNotification.setWebhook("/consequence/update");
		consequences.add(directSlackNotification);
		consequences.add(publicSlackNotification);
		return consequences;
	}

}
