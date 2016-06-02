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

package com.bjond.adapter.push.services.exceptionmapper;

// Java
import java.util.List;

// Java
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.bjond.constants.ErrorCodes;
import com.bjond.utilities.NetworkUtils;

import lombok.extern.slf4j.Slf4j;


/** <p> 
    Maps any uncaught system exception from RESTEASY to a human
    readable small error message. 
    JSR311 https://jsr311.java.net/nonav/javadoc/javax/ws/rs/ext/ExceptionMapper.html
    http://docs.jboss.org/resteasy/docs/1.1.GA/userguide/html/ExceptionHandling.html
    </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */

@Provider
@Slf4j
public class BjondExceptionMapperEJBTransactionRolledback implements ExceptionMapper<javax.ejb.EJBTransactionRolledbackException> {
    public Response toResponse(javax.ejb.EJBTransactionRolledbackException e) {
        log.error(ExceptionUtils.getMessage(e), e);

        final List<Throwable> list = ExceptionUtils.getThrowableList(e);
        final boolean bConstraintViolation = list.stream().anyMatch(t -> t instanceof org.hibernate.exception.ConstraintViolationException);

        final ErrorCodes.BJOND_HTTP_ERROR_CODES code = (bConstraintViolation) ? ErrorCodes.BJOND_HTTP_ERROR_CODES.CONSTRAINT_VIOLATION : ErrorCodes.BJOND_HTTP_ERROR_CODES.UNEXPECTED_ERROR;

        // If there was a constraint violation on the server
        // find the native exception string.
        String nativeMessage = "";
        if(bConstraintViolation) {
            // The PSQLException actually thrown by the system is in a JDBC driver which will almost certainly
            // differ from a maven retrieved JAR file so the instanceof will fail. It is safer to match strings. 
            final Throwable item = list.stream().filter(t -> t.toString().startsWith("org.postgresql.util.PSQLException", 0)).findFirst().get();
            if(item != null) {
                nativeMessage = item.getMessage();
            } 
        }
        
        return NetworkUtils.errorResponse(Response.Status.INTERNAL_SERVER_ERROR, ExceptionUtils.getMessage(e), nativeMessage, code);

    }
}
