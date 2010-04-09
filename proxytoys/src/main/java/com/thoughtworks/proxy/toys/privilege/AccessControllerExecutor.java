/*
 * (c) 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 19-03-2010.
 */
package com.thoughtworks.proxy.toys.privilege;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;


/**
 * Execution of a {@link PrivilegedExceptionAction} with the {@link AccessController}.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public class AccessControllerExecutor implements ActionExecutor {
    private final AccessControlContext context;

    /**
     * Construct an AccessControllerExecutor that runs a {@link PrivilegedExceptionAction} with
     * the privileges of the {@link AccessControlContext} of the caller.
     * 
     * @since 1.0
     */
    public AccessControllerExecutor() {
        this(null);
    }

    /**
     * Construct an AccessControllerExecutor that runs a {@link PrivilegedExceptionAction} with
     * special privileges.
     * 
     * @param context the {@link AccessControlContext} defining the privileges used to run the
     *            action
     * @since 1.0
     */
    public AccessControllerExecutor(AccessControlContext context) {
        this.context = context;
    }

    public Object execute(PrivilegedExceptionAction<Object> action)
        throws PrivilegedActionException {
        return AccessController.doPrivileged(action, context);
    }
}
