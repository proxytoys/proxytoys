/*
 * Created on 04-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import java.lang.reflect.Method;

/**
 * Decorates a method invocation
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public interface InvocationDecorator {

    /**
     * Called before a method is invoked on an object, to possibly
     * decorate the arguments being passed to the method invocation.
     * 
     * @param target the object the method will be invoked on
     * @param method the method to be invoked
     * @param args the arguments being passed to the method
     * @return the decorated arguments (typically just the ones supplied)
     */
	Object[] beforeMethodStarts(Object target, Method method, Object[] args);

    /**
     * Called on the way back from a method invocation, to possibly
     * decorate the result.
     * 
     * @param result the result of the method invocation
     * @return the decorated result (typically just the supplied result)
     */
	Object decorateResult(Object result);

    /**
     * Called when a method invocation fails, to possibly decorate
     * the type of error.
     * 
     * @param cause the original exception thrown
     * @return the decorated exception (typically just the supplied cause)
     */
	Throwable decorateException(Throwable cause);
}