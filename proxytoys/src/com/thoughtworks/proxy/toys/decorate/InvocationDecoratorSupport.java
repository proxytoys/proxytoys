/*
 * Created on 10-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import java.lang.reflect.Method;

/**
 * Identity implementation for a InvokerDecorator. The implementation will just pass through any values. Use this
 * as base class for derived implementations, that do not override all of the methods.
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @since 0.1
 */
public class InvocationDecoratorSupport implements InvocationDecorator {
	public Object[] beforeMethodStarts(Object proxy, Method method, Object[] args) {
        return args;
	}

    public Object decorateResult(Object proxy, Method method, Object[] args, Object result) {
		return result;
	}

	public Throwable decorateTargetException(Object proxy, Method method, Object[] args, Throwable cause) {
		return cause;
	}

	public Exception decorateInvocationException(Object proxy, Method method, Object[] args, Exception cause) {
        return cause;
	}
}
