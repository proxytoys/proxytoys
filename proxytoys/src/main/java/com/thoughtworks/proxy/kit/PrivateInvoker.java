/*
 * Created on 21-Jul-2005
 *
 * (c) 2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.kit;

import java.lang.reflect.Method;


/**
 * An Invoker that is able to call protected and private methods. If the proxies type is an abstract class, it might be
 * necessary to use an PrivateInvoker instead of a SimpleInvoker. Otherwise the method implementations may not call the
 * protected or private methods of the own type.
 *
 * @author J&ouml;rg Schaible
 */
public class PrivateInvoker extends SimpleInvoker {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a PrivateInvoker.
     *
     * @param target the invocation target.

     */
    public PrivateInvoker(final Object target) {
        super(target);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (getTarget() != null && !method.isAccessible()) {
            method.setAccessible(true);
        }
        return super.invoke(proxy, method, args);
    }

}
