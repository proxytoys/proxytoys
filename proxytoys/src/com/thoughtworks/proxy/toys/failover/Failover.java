/*
 * Created on 11-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.failover;

import com.thoughtworks.proxy.ProxyFactory;


/**
 * Factory for proxy instances handling failover.. Delegates to one object as long as there is no exception, fails over to the
 * next when an exception occurs.
 * 
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public class Failover {

    /**
     * Create a proxy of a specific type with failover capability using the given objects. The provided exception type determins
     * the type of exceptions that trigger the failover.
     * 
     * @param type the type of the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param delegates the delegates used for failover
     * @param exceptionClass the type of the exceptions triggering failover
     * @return the generated proxy
     */
    public static Object object(
            final Class type, final ProxyFactory proxyFactory, final Object[] delegates, final Class exceptionClass) {
        return object(new Class[]{type}, proxyFactory, delegates, exceptionClass);
    }

    /**
     * Create a proxy of a specific types with failover capability using the given objects. The provided exception type
     * determins the type of exceptions that trigger the failover.
     * 
     * @param types the implemented types of the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param delegates the delegates used for failover
     * @param exceptionClass the type of the exceptions triggering failover
     * @return the generated proxy
     */
    public static Object object(
            final Class[] types, final ProxyFactory proxyFactory, final Object[] delegates, final Class exceptionClass) {
        return new FailoverInvoker(types, proxyFactory, delegates, exceptionClass).proxy();
    }

    /** It's a factory, stupid */
    private Failover() {
    }
}