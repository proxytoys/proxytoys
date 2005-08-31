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
 * Identity implementation for a InvokerDecorator. The implementation will just pass through any values. Use this as base class
 * for derived implementations, that do not override all of the methods.
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @since 0.1
 */
public class InvocationDecoratorSupport implements InvocationDecorator {
    private static final long serialVersionUID = 1L;

    public Object[] beforeMethodStarts(final Object proxy, final Method method, final Object[] args) {
        return args;
    }

    public Object decorateResult(final Object proxy, final Method method, final Object[] args, final Object result) {
        return result;
    }

    public Throwable decorateTargetException(final Object proxy, final Method method, final Object[] args, final Throwable cause) {
        return cause;
    }

    public Exception decorateInvocationException(final Object proxy, final Method method, final Object[] args, final Exception cause) {
        return cause;
    }
}
