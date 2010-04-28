/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10-May-2004
 */
package com.thoughtworks.proxy.toys.decorate;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Identity implementation for a Decorator. The implementation will just pass through any values. Override all methods
 * that should behave differently.
 *
 * @author Dan North
 * @since 1.0
 */
public abstract class Decorator<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Called before a method is invoked on an object, to possibly decorate the arguments being passed to the method
     * invocation.
     *
     * @param proxy  the proxy the method will be invoked on
     * @param method the method to be invoked
     * @param args   the arguments being passed to the method
     * @return the decorated arguments (typically just the ones supplied)
     * @since 1.0
     */
    public Object[] beforeMethodStarts(final T proxy, final Method method, final Object[] args) {
        return args;
    }

    /**
     * Called on the way back from a method invocation, to possibly decorate the result.
     *
     * @param proxy  the proxy the method was be invoked on
     * @param method the invoked method
     * @param args   the arguments passed to the method
     * @param result the result of the method invocation
     * @return the decorated result (typically just the supplied result)
     * @since 1.0
     */
    public Object decorateResult(final T proxy, final Method method, final Object[] args, final Object result) {
        return result;
    }

    /**
     * Called when a called method fails, to possibly decorate the type of error.
     *
     * @param proxy  the proxy the method was be invoked on
     * @param method the invoked method
     * @param args   the arguments passed to the method
     * @param cause  the original exception thrown
     * @return the decorated exception (typically just the supplied cause)
     * @since 1.0
     */
    public Throwable decorateTargetException(
            final T proxy, final Method method, final Object[] args, final Throwable cause) {
        return cause;
    }

    /**
     * Called when a method cannot be invoked, to possibly decorate the type of error.
     *
     * @param proxy  the proxy the method was be invoked on
     * @param method the invoked method
     * @param args   the arguments passed to the method
     * @param cause  the original exception thrown
     * @return the decorated exception (typically just the supplied cause)
     * @since 1.0
     */
    public Exception decorateInvocationException(
            final T proxy, final Method method, final Object[] args, final Exception cause) {
        return cause;
    }
}
