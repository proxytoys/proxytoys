/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 21-Jul-2005
 */
package com.thoughtworks.proxy.kit;

import java.lang.reflect.Method;


/**
 * An Invoker that is able to call protected and private methods. If the proxies type is an abstract class, it might be
 * necessary to use an PrivateInvoker instead of a SimpleInvoker. Otherwise the method implementations may not call the
 * protected or private methods of the own type.
 *
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public class PrivateInvoker extends SimpleInvoker {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a PrivateInvoker.
     *
     * @param target the invocation target.
     * @since 0.2
     */
    public PrivateInvoker(final Object target) {
        super(target);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (getTarget() != null && !method.isAccessible()) {
            method.setAccessible(true);
        }
        return super.invoke(proxy, method, args);
    }

}
