/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 23-May-2004
 */
package com.thoughtworks.proxy.kit;

import java.lang.reflect.Method;

import com.thoughtworks.proxy.Invoker;


/**
 * A simple {@link Invoker} implementation, that routes any call to a target object. A <code>null</code> value as
 * target can be handled, the invocation result will always be <code>null</code>.
 *
 * @author Aslak Helles&oslash;y
 * @since 0.2
 */
public class SimpleInvoker implements Invoker {

    private static final long serialVersionUID = 1L;

    private Object target;

    /**
     * Construct a SimpleInvoker.
     *
     * @param target the invocation target.
     * @since 0.2
     */
    public SimpleInvoker(final Object target) {
        this.target = target;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return (target == null ? null : method.invoke(target, args));
    }

    /**
     * Retrieve the target of the invocations.
     *
     * @return the target object
     * @since 0.2
     */
    protected Object getTarget() {
        return target;
    }
}
