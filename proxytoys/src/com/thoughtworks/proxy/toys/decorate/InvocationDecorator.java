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
 * @since 0.1
 */
public interface InvocationDecorator {

    /**
     * Called before a method is invoked on an object, to possibly decorate the arguments being passed to the method invocation.
     * 
     * @param proxy the proxy the method will be invoked on
     * @param method the method to be invoked
     * @param args the arguments being passed to the method
     * @return the decorated arguments (typically just the ones supplied)
     * @since 0.1
     */
    Object[] beforeMethodStarts(Object proxy, Method method, Object[] args);

    /**
     * Called on the way back from a method invocation, to possibly decorate the result.
     * 
     * @param proxy the proxy the method was be invoked on
     * @param method the invoked method
     * @param args the arguments passed to the method
     * @param result the result of the method invocation
     * @return the decorated result (typically just the supplied result)
     * @since 0.2, different arguments in 0.1
     */
    Object decorateResult(Object proxy, Method method, Object[] args, Object result);

    /**
     * Called when a called method fails, to possibly decorate the type of error.
     * 
     * @param proxy the proxy the method was be invoked on
     * @param method the invoked method
     * @param args the arguments passed to the method
     * @param cause the original exception thrown
     * @return the decorated exception (typically just the supplied cause)
     * @since 0.2, different arguments in 0.1
     */
    Throwable decorateTargetException(Object proxy, Method method, Object[] args, Throwable cause);

    /**
     * Called when a method cannot be invoked, to possibly decorate the type of error.
     * 
     * @param proxy the proxy the method was be invoked on
     * @param method the invoked method
     * @param args the arguments passed to the method
     * @param cause the original exception thrown
     * @return the decorated exception (typically just the supplied cause)
     * @since 0.2, different arguments in 0.1
     */
    Exception decorateInvocationException(Object proxy, Method method, Object[] args, Exception cause);
}