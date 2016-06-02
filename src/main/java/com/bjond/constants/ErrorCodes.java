/*  Copyright (c) 2015
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

package com.bjond.constants;


/** <p> Maintains Error Codes in the system </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */


public class ErrorCodes {

	public enum BJOND_HTTP_ERROR_CODES {
	    UNKNOWN_USER,
	    UNKNOWN_TENANT,
	    INVALID_TENANT,
	    TAMPERING_DETECTED,
	    UNEXPECTED_ERROR,
	    AUTHENTICATION_FAILED,
	    PASSWORD_EXPIRED,
	    ACCOUNT_EXPIRED,
	    ACCOUNT_LOCKED,
	    AUTHORIZATION_FAILED,
	    MAX_PASSWORD_FAILURES_REACHED,
	    DATABASE_ERROR,
	    REST_INTERFACE_DOES_NOT_EXIST,
	    SESSION_DOES_NOT_EXIST,
	    CYCLIC_DEPENDENCY,
        VALIDATION_ERROR,
        CONSTRAINT_VIOLATION,
        BJOND_SERVER_COMMUNICATION_FAILURE
	}


}
