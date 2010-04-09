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

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Interface to execute a {@link PrivilegedExceptionAction}. Implementations of this type are used by the
 * {@linkplain Privileging privileging toy}.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public interface ActionExecutor
{
    /**
     * Execute a {@link PrivilegedExceptionAction}.
     * 
     * @param action the action to run
     * @return the return value of the action
     * @throws PrivilegedActionException if the execution of the action fails
     * @since 1.0
     */
    Object execute(PrivilegedExceptionAction<Object> action) throws PrivilegedActionException;
}
