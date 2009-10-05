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
     * @param type the types of the proxy
     * @return a factory that will proxy instances of the supplied type.
     */
    public static <T> FailoverWithOrExceptingOrBuild<T> failoverable(Class<T> type) {

        return new FailoverWithOrExceptingOrBuild<T>(new Failover<T>(type));
    }

    public static class FailoverWithOrExceptingOrBuild<T> extends FailoverExceptingOrBuild<T> {

        private FailoverWithOrExceptingOrBuild(Failover<T> failover) {
            super(failover);
        }

        /**
         * With these delegates
         *
         * @param delegates the delegates used for failover
         * @return
         */
        public FailoverExceptingOrBuild<T> with(final Object... delegates) {
            failover.delegates = delegates;
            return new FailoverExceptingOrBuild<T>(failover);
        }

    }

    public static class FailoverExceptingOrBuild<T> extends FailoverBuild<T> {

        private FailoverExceptingOrBuild(Failover<T> failover) {
            super(failover);
        }

        /**
         * Excepting this exception class
         *
         * @param exceptionClass the type of the exceptions triggering failover
         * @return
         */
        public FailoverBuild<T> excepting(Class<? extends Throwable> exceptionClass) {
            failover.exceptionClass = exceptionClass;
            return new FailoverBuild(failover);

        }

    }

    public static class FailoverBuild<T> {
        protected Failover<T> failover;

        private FailoverBuild(Failover<T> failover) {
            this.failover = failover;
        }

        /**
         * * Create a proxy of a specific types with failover capability using the given objects.  The provided exception type
         * determins the type of exceptions that trigger the failover.
         *
         * @param proxyFactory the {@link ProxyFactory} to use
         * @return the created proxy
         */
        public T build(final ProxyFactory proxyFactory) {
            return (T) new FailoverInvoker(new Class[]{failover.type}, proxyFactory, failover.delegates, failover.exceptionClass).proxy();
        }

    }


}