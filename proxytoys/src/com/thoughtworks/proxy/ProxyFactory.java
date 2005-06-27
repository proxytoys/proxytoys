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
 * Abstraction layer for proxy generation. Depending on this interface
 * (rather than {@link java.lang.reflect.Proxy} directly) will allow
 * you to use Java's standard proxy mechanism interchangeably with e.g. CGLIB.
 *
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public interface ProxyFactory extends Serializable {
    /**
     * Create a new proxy instance.
     * @param types The types the proxy must emulate.
     * @param invoker The invocation handler.
     * @return Returns the new proxy instance.
     */
    Object createProxy(Class[] types, Invoker invoker);
    /**
     * Test if the ProxyFactory implementation is capable of creating a proxy instance for the given type.
     * @param type The type to create a proxy instance for.
     * @return Returns <code>true</code> if the type is supported.
     */
    boolean canProxy(Class type);
    /**
     * Test if the given type is a proxy class.
     * @param type The type to examin.
     * @return Returns <code>true</code> if the given type is a proxy class.
     */
    boolean isProxyClass(Class type);
    /**
     * Retrieve the invocation handler of the proxy.
     * @param proxy The proxy instance.
     * @return The {@link Invoker} instance acting as invocation handler.
     */
    Invoker getInvoker(Object proxy);
}