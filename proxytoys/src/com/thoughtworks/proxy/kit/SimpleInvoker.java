/*
 * Created on 23-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.kit;

import com.thoughtworks.proxy.Invoker;

import java.lang.reflect.Method;


/**
 * A simple {@link Invoker} implementation, that routes any call to a target object. A <code>null</code> value as target can
 * be handled, the invocation result will alway be <code>null</code>.
 * 
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public class SimpleInvoker implements Invoker {
    private final Object target;

    /**
     * Construct a SimpleInvoker.
     * 
     * @param target the invocation target.
     */
    public SimpleInvoker(final Object target) {
        this.target = target;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return (target == null ? null : method.invoke(target, args));
    }
}
