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

    /**
     * Creates a factory for proxy instances handling failover.
     *
     * @param type           the types of the proxy
     * @return a factory that will proxy instances of the supplied type.
     * @since 0.2
     */
    public static <T> Failover<T> failoverable(Class<T> type) {

        return new Failover<T>(type);
    }

    /**
     * With these delegates
     * @param delegates      the delegates used for failover
     * @return
     */
    public Failover<T> with(final Object... delegates) {
        this.delegates = delegates;
        return this;
    }


    /**
     * Excepting this exception class
     * @param exceptionClass the type of the exceptions triggering failover
     * @return
     */
    public Failover<T> excepting(Class<? extends Throwable> exceptionClass) {
        this.exceptionClass = exceptionClass;
        return this;
        
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