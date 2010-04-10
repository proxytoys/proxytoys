/*
 * (c) 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10-04-2010.
 */
package com.thoughtworks.proxy.toys.privilege;

import java.security.AccessControlContext;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;


/**
 * Execution of a {@link PrivilegedExceptionAction} with a {@link Subject}.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public class SubjectExecutor implements ActionExecutor {
    private final AccessControlContext context;
    private final Subject subject;

    /**
     * Construct a SubjectExecutor that runs a {@link PrivilegedExceptionAction} with
     * the {@link Subject#doAs(Subject, PrivilegedExceptionAction)} method.
     * 
     * @param subject the subject used to run the methods 
     * @since 1.0
     */
    public SubjectExecutor(Subject subject) {
        this(subject, null);
    }

    /**
     * Construct a SubjectExecutor that runs a {@link PrivilegedExceptionAction} with
     * the {@link Subject#doAsPrivileged(Subject, PrivilegedExceptionAction, AccessControlContext)} method.
     * 
     * @param subject the subject used to run the methods 
     * @param context the {@link AccessControlContext} defining privileges for the subject
     * @since 1.0
     */
    public SubjectExecutor(Subject subject, AccessControlContext context) {
        this.subject = subject;
        this.context = context;
    }

    public Object execute(PrivilegedExceptionAction<Object> action)
        throws PrivilegedActionException {
        if (context == null) {
            return Subject.doAs(subject, action);
        } else {
            return Subject.doAsPrivileged(subject, action, context);
        }
    }
}
