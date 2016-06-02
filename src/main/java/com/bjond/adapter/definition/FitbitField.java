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

import java.util.Set;

import lombok.Data;

@Data
public class FitbitField {
    private String id;
	private String jsonKey;
	private String name;
	private String description;
	private String fieldType;
	private String event;
	private Set<String> options;
}
