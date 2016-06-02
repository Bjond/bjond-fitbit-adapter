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
	
    final private static String ADAPTER_PUBLIC_URL = System.getenv("SLACK_ADAPTER_PUBLIC_URL");
    final public static String ADAPTER_ID = "be4025e4-e121-4548-9f8c-31d318c5876b";
    final public static String SLASH_EVENT_ID = "d2fbf64c-72d8-4e43-98cf-7d06ed68c086";
	
	public static FitbitAdapter getDefinition() {
		FitbitAdapter adapter = new FitbitAdapter();
		adapter.setAuthor("Bjönd, Inc");
		adapter.setId(ADAPTER_ID);
		adapter.setName("Slack");
		adapter.setRootEndpoint(StringUtils.defaultIfBlank(ADAPTER_PUBLIC_URL, "http://localhost:8080") + "/bjond-slack-adapter/services/adapter");
		adapter.setIconURL("https://upload.wikimedia.org/wikipedia/en/7/76/Slack_Icon.png");
		adapter.setDescription("You will be allowed to send Slack notifications from your rules with this adapter.");
		adapter.setIntegrationEvent(getEvents(adapter.getId()));
		adapter.setIntegrationConsequence(getConsequences(adapter.getId()));
		return adapter;
	}
	
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
	
	private static Set<FitbitField> getFields(String event, String eventid) {
		Set<FitbitField> fields = new HashSet<FitbitField>();
		switch (event){
        case "slash":
        	FitbitField userField = new FitbitField();
        	userField.setId("a2b1d4e9-85df-4302-87a1-0c9a66e5cea7");
        	userField.setFieldType("Person");
        	userField.setJsonKey("person");
        	userField.setName("Person");
        	userField.setDescription("The person who issued the command.");
        	userField.setEvent(eventid);
        	fields.add(userField);
        	
        	FitbitField command = new FitbitField();
        	command.setId("df474d66-39fb-4311-a9bd-e798cbb08329");
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
	
	private static Set<FitbitConsequence> getConsequences(String adapterid) {
		Set<FitbitConsequence> consequences = new HashSet<>();
		FitbitConsequence directSlackNotification = new FitbitConsequence();
		directSlackNotification.setId("15b942b4-4d65-46a4-8de6-17cd8a163ee9");
		directSlackNotification.setDescription("Sends a notification directly to the slack user.");
		directSlackNotification.setJsonKey("slack-direct-notification");
		directSlackNotification.setName("Slack Direct Notification");
		directSlackNotification.setServiceid(adapterid);
		directSlackNotification.setWebhook("/consequence/direct");
		
		FitbitConsequence publicSlackNotification = new FitbitConsequence();
		publicSlackNotification.setId("f141eb0c-271a-4c87-a9f4-96ccc32921e0");
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
