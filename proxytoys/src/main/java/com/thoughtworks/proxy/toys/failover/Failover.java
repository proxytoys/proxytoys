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

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;

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
    private Class<?>[] types;
    private T[] delegates;
    private Class<? extends Throwable> exceptionClass;

    private Failover(Class<T> primaryType, Class<?>... types) {
        this.types = ReflectionUtils.makeTypesArray(primaryType, types);
    }

    /**
     * Creates a factory for proxy instances handling failover situations.
     *
     * @param type the types of the proxy
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <T> FailoverWithOrExceptingOrBuild<T> proxy(Class<T> type) {
        return new FailoverWithOrExceptingOrBuild<T>(new Failover<T>(type));
    }
    
    /**
     * Creates a factory for proxy instances handling failover situations.
     *
     * @param primaryType the primary type implemented by the proxy
     * @param types other types that are implemented by the proxy
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <T> FailoverWithOrExceptingOrBuild<T> proxy(final Class<T> primaryType, final Class<?> ... types) {
        return new FailoverWithOrExceptingOrBuild<T>(new Failover<T>(primaryType, types));
    }
    
    /**
     * Creates a factory for proxy instances handling failover situations.
     *
     * @param delegates the array with the delegates in a failover situation
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <T> FailoverExceptingOrBuild<T> proxy(final T... delegates) {
        Failover<T> failover = new Failover<T>(null);
        failover.delegates = delegates;
        return new FailoverExceptingOrBuild<T>(failover);
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
            return new FailoverInvoker<T>(failover.types, proxyFactory, failover.delegates, failover.exceptionClass).proxy();
        }
    }
}