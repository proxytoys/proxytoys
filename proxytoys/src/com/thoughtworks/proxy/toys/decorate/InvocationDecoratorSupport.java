/*
 * Created on 10-May-2004
 *
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class InvocationDecoratorSupport implements InvocationDecorator {
	public Object[] beforeMethodStarts(Object target, Method method, Object[] args) {
        return args;
	}

    public Object decorateResult(Object result) {
		return result;
	}

	public Throwable decorateTargetException(Throwable cause) {
		return cause;
	}

	public Exception decorateInvocationException(Exception cause) {
        return cause;
	}
}
