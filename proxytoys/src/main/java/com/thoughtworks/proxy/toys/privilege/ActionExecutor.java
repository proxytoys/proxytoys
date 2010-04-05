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

public interface ActionExecutor
{
    Object execute(PrivilegedExceptionAction<Object> action) throws PrivilegedActionException;
}
