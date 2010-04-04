/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11-May-2004
 */
package com.thoughtworks.proxy.toys.failover;

import com.thoughtworks.proxy.ProxyFactory;

/**
 * Factory for proxy instances handling failover. Delegates to one object as long as there is no exception, fails over
 * to the next when an exception occurs.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @see com.thoughtworks.proxy.toys.failover
 * @since 0.1
 */
public class Failover<T> {
    private Class<T> type;
    private T[] delegates;
    private Class<? extends Throwable> exceptionClass;

    private Failover(Class<T> type) {
        this.type = type;
    }

    /**
     * Creates a factory for proxy instances handling multicast.
     *
     * @param type the types of the proxy
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <T> FailoverWithOrExceptingOrBuild<T> proxy(Class<T> type) {
        return new FailoverWithOrExceptingOrBuild<T>(new Failover<T>(type));
    }
    
    public static <T> FailoverWithOrExceptingOrBuild<T> proxy(final Class<T> primaryType, final Class<?> ... types) {
        // TODO: Provide this functionality again
        throw new UnsupportedOperationException("TODO");
    }
    public static <T> FailoverExceptingOrBuild<T> proxy(final T object, final Object ... more) {
        // TODO: Provide this functionality again
        throw new UnsupportedOperationException("TODO");
    }

    public static class FailoverWithOrExceptingOrBuild<T> extends FailoverExceptingOrBuild<T> {

        private FailoverWithOrExceptingOrBuild(Failover<T> failover) {
            super(failover);
        }

        /**
         * With these delegates.
         *
         * @param delegates the delegates used for failover
         * @return a factory that will use the supplied delegates in case of a failure.
         * @since 1.0
         */
        public FailoverExceptingOrBuild<T> with(final T... delegates) {
            failover.delegates = delegates;
            return new FailoverExceptingOrBuild<T>(failover);
        }
    }

    public static class FailoverExceptingOrBuild<T> extends FailoverBuild<T> {

        private FailoverExceptingOrBuild(Failover<T> failover) {
            super(failover);
        }

        /**
         * Excepting this exception class.
         *
         * @param exceptionClass the type of the exceptions triggering failover
         * @return a factory that will trigger the usage of the next delegate based on the supplied Throwable type.
         * @since 1.0
         */
        public FailoverBuild<T> excepting(Class<? extends Throwable> exceptionClass) {
            failover.exceptionClass = exceptionClass;
            return new FailoverBuild<T>(failover);
        }
    }

    public static class FailoverBuild<T> {
        protected Failover<T> failover;

        private FailoverBuild(Failover<T> failover) {
            this.failover = failover;
        }

        /**
         * Create a proxy of a specific types with failover capability using the given objects.  The provided exception type
         * determines the type of exceptions that trigger the failover.
         *
         * @param proxyFactory the {@link ProxyFactory} to use
         * @return the created proxy
         * @since 1.0
         */
        public T build(final ProxyFactory proxyFactory) {
            return new FailoverInvoker<T>(new Class[]{failover.type}, proxyFactory, failover.delegates, failover.exceptionClass).proxy();
        }
    }
}