/*
 * Created 19.03.2010 by Joerg Schaible.
 * 
 * (c) 2010 ThoughtWorks
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.privilege;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class AccessControllerExecutor implements ActionExecutor
{
	private final AccessControlContext context;

	public AccessControllerExecutor() {
		this(null);
	}

	public AccessControllerExecutor(AccessControlContext context) {
		this.context = context;
	}

	public Object execute(PrivilegedExceptionAction<Object> action) throws PrivilegedActionException
	{
		return AccessController.doPrivileged(action, context);
	}
}
