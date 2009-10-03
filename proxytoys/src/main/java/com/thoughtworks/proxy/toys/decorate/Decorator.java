/*
 * Created on 10-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Identity implementation for a InvokerDecorator. The implementation will just pass through any values. Use this as
 * base class for derived implementations, that do not override all of the methods.
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public abstract class Decorator implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Called before a method is invoked on an object, to possibly decorate the arguments being passed to the method
     * invocation.
     *
     * @param proxy  the proxy the method will be invoked on
     * @param method the method to be invoked
     * @param args   the arguments being passed to the method
     * @return the decorated arguments (typically just the ones supplied)
     */
    public Object[] beforeMethodStarts(final Object proxy, final Method method, final Object[] args) {
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
     */
    public Object decorateResult(final Object proxy, final Method method, final Object[] args, final Object result) {
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
     */
    public Throwable decorateTargetException(
            final Object proxy, final Method method, final Object[] args, final Throwable cause) {
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
     */
    public Exception decorateInvocationException(
            final Object proxy, final Method method, final Object[] args, final Exception cause) {
        return cause;
    }
}
