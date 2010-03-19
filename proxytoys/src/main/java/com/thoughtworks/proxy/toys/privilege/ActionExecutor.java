/*
 * Created 19.03.2010 by Joerg Schaible.
 * 
 * (c) 2010 ThoughtWorks
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.privilege;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public interface ActionExecutor
{
	Object execute(PrivilegedExceptionAction<Object> action) throws PrivilegedActionException;
}
