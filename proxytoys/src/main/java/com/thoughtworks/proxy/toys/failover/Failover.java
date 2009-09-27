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
 * Factory for proxy instances handling failover.. Delegates to one object as long as there is no exception, fails over
 * to the next when an exception occurs.
 *
 * @author Aslak Helles&oslash;y
 * @see com.thoughtworks.proxy.toys.failover
 * @since 0.1
 */
public class Failover<T> {
    private Class<T> type;
    private Object[] delegates;
    private Class exceptionClass;

    public Failover(Class<T> type) {
        this.type = type;
    }

    public Failover(Class<T> type, Object[] delegates, Class exceptionClass) {
        this.type = type;
        this.delegates = delegates;
        this.exceptionClass = exceptionClass;
    }

    /**
     * Creates a factory for proxy instances that allow delegation.
     *
     * @param type           the types of the proxy
     * @param delegates      the delegates used for failover
     * @param exceptionClass the type of the exceptions triggering failover
     * @return a factory that will proxy instances of the supplied type.
     * @since 0.2
     */
    public static <T> Failover<T> failoverable(Class<T> type, final Object[] delegates, final Class exceptionClass) {

        return new Failover<T>(type, delegates, exceptionClass);
    }


    /**
     * * Create a proxy of a specific types with failover capability using the given objects.  The provided exception type
     * determins the type of exceptions that trigger the failover.
     *
     * @param proxyFactory the {@link ProxyFactory} to use
     * @return the created proxy
     * @since 0.2
     */
    public T build(final ProxyFactory proxyFactory) {
        return (T) new FailoverInvoker(new Class[]{type}, proxyFactory, delegates, exceptionClass).proxy();
    }

}