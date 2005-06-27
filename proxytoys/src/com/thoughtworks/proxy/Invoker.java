/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Generic interface for any call made to a proxy instance.
 * This is the main interface for any proxy implementation using a {@link ProxyFactory}.
 * An implementation realizes an invocation handler for the proxy. So it has the same
 * purpose as {@link java.lang.reflect.InvocationHandler}.
 * @since 0.1
 */
public interface Invoker extends Serializable {
    /**
     * Invocation of a method of the proxied object.
     * @param proxy The proxy instance.
     * @param method The method to invoke.
     * @param args The arguments of the mothod.
     * @return Returns the result of the onvoked method.
     * @throws Throwable Thrown if the invoked method has thrown.
     */
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}