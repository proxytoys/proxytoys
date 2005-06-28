/*
 * Created on 04-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
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
     * @param proxy The proxy the method will be invoked on
     * @param method The method to be invoked
     * @param args The arguments being passed to the method
     * @return Returns the decorated arguments (typically just the ones supplied)
     */
	Object[] beforeMethodStarts(Object proxy, Method method, Object[] args);

    /**
     * Called on the way back from a method invocation, to possibly
     * decorate the result.
     * 
     * @param proxy The proxy the method was be invoked on
     * @param method The invoked method
     * @param args The arguments passed to the method
     * @param result The result of the method invocation
     * 
     * @return Returns the decorated result (typically just the supplied result)
     */
	Object decorateResult(Object proxy, Method method, Object[] args, Object result);

    /**
     * Called when a called method fails, to possibly decorate
     * the type of error.
     * 
     * @param proxy The proxy the method was be invoked on
     * @param method The invoked method
     * @param args The arguments passed to the method
     * @param cause The original exception thrown
     * @return Returns the decorated exception (typically just the supplied cause)
     */
	Throwable decorateTargetException(Object proxy, Method method, Object[] args, Throwable cause);

    /**
     * Called when a method cannot be invoked, to possibly decorate
     * the type of error.
     * 
     * @param proxy The proxy the method was be invoked on
     * @param method The invoked method
     * @param args The arguments passed to the method
     * @param cause The original exception thrown
     * @return Returns the decorated exception (typically just the supplied cause)
     */
	Exception decorateInvocationException(Object proxy, Method method, Object[] args, Exception cause);
}