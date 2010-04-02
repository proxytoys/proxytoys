/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03-May-2004
 */
package com.thoughtworks.proxy;

import java.io.Serializable;


/**
 * Abstraction layer for proxy generation. Depending on this interface (rather than {@link java.lang.reflect.Proxy}
 * directly) will allow you to use Java's standard proxy mechanism interchangeably with e.g. CGLIB.
 *
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public interface ProxyFactory extends Serializable {

    /**
     * Create a new proxy instance.
     * 
     * @param <T> The proxy's type. 
     * @param invoker the invocation handler.
     * @param types   the types the proxy must emulate.
     * @return the new proxy instance.
     * @since 1.0
     */
    <T> T createProxy(Invoker invoker, Class<?>... types);

    /**
     * Test if the ProxyFactory implementation is capable of creating a proxy instance for the given type.
     *
     * @param type the type to create a proxy instance for.
     * @return <code>true</code> if the type is supported.
     * @since 0.1
     */
    boolean canProxy(Class<?> type);

    /**
     * Test if the given type is a proxy class.
     *
     * @param type the type to examine.
     * @return <code>true</code> if the given type is a proxy class.
     * @since 0.1
     */
    boolean isProxyClass(Class<?> type);

    /**
     * Retrieve the invocation handler of the proxy.
     *
     * @param proxy the proxy instance.
     * @return the {@link Invoker} instance acting as invocation handler.
     * @since 0.1
     */
    Invoker getInvoker(Object proxy);
}