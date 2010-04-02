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
import java.lang.reflect.Method;


/**
 * Generic interface for any call made to a proxy instance. This is the main interface for any proxy implementation
 * using a {@link ProxyFactory}. An implementation realizes an invocation handler for the proxy. So it has the same
 * purpose as {@link java.lang.reflect.InvocationHandler}.
 *
 * @since 0.1
 */
public interface Invoker extends Serializable {
    
    /**
     * Invocation of a method of the proxied object.
     *
     * @param proxy  the proxy instance.
     * @param method the method to invoke.
     * @param args   the arguments of the method.
     * @return the result of the invoked method.
     * @throws Throwable if the invoked method has thrown.
     * @since 0.1
     */
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}