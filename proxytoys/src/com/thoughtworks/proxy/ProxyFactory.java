/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy;

import java.io.Serializable;


/**
 * Abstraction layer for proxy generation. Depending on this interface (rather than {@link java.lang.reflect.Proxy} directly)
 * will allow you to use Java's standard proxy mechanism interchangeably with e.g. CGLIB.
 * 
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public interface ProxyFactory extends Serializable {
    /**
     * Create a new proxy instance.
     * 
     * @param types the types the proxy must emulate.
     * @param invoker the invocation handler.
     * @return the new proxy instance.
     */
    Object createProxy(Class[] types, Invoker invoker);

    /**
     * Test if the ProxyFactory implementation is capable of creating a proxy instance for the given type.
     * 
     * @param type the type to create a proxy instance for.
     * @return <code>true</code> if the type is supported.
     */
    boolean canProxy(Class type);

    /**
     * Test if the given type is a proxy class.
     * 
     * @param type the type to examin.
     * @return <code>true</code> if the given type is a proxy class.
     */
    boolean isProxyClass(Class type);

    /**
     * Retrieve the invocation handler of the proxy.
     * 
     * @param proxy the proxy instance.
     * @return the {@link Invoker} instance acting as invocation handler.
     */
    Invoker getInvoker(Object proxy);
}