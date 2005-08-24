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
 * @since 0.2, 0.1 in package com.thoughtworks.proxy.toy.decorate
 */
public class SimpleInvoker implements Invoker {
    private static final long serialVersionUID = 1L;

    /** the target of the invocations. */
    protected final Object target;

    /**
     * Construct a SimpleInvoker.
     * 
     * @param target the invocation target.
     * @since 0.2, 0.1 in package com.thoughtworks.proxy.toy.decorate
     */
    public SimpleInvoker(final Object target) {
        this.target = target;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return (target == null ? null : method.invoke(target, args));
    }
}
