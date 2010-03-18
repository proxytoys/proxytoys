/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 * 
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.delegate;

import static com.thoughtworks.proxy.toys.delegate.DelegationMode.SIGNATURE;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.SimpleReference;

/**
 * Toy factory to create proxies delegating to another object.
 * <p>
 * Such a proxy is used to mask the methods of an object, that are not part of a public interface. Or it is used to make
 * an object compatible, e.g. when an object implements the methods of an interface, but does not implement the
 * interface itself.
 * </p>
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @see com.thoughtworks.proxy.toys.delegate
 */
public class Delegating<T> {

    private Class<T> type;
    private Object delegate;
    private DelegationMode delegationMode = SIGNATURE;

    private Delegating(Class<T> type) {
        this.type = type;
    }

    /**
     * Creates a factory for proxy instances that allow delegation.
     *
     * @param type     the type of the proxy when it is finally created.
     * @return a factory that will proxy instances of the supplied type.
     */

    public static <T> DelegatingWith<T> delegatable(Class<T> type) {
        return new DelegatingWith<T>(new Delegating<T>(type));
    }

    public static class DelegatingWith<T> {
        private Delegating<T> delegating;

        private DelegatingWith(Delegating<T> delegating) {
            this.delegating = delegating;
        }

        /**
         * With this delegate
         *
         * @param delegate the object the proxy delegates to.
         * @return the factory that will route calls to the supplied delegate.
         */
        public DelegatingModeOrBuild<T> with(Object delegate) {
            delegating.delegate = delegate;
            return new DelegatingModeOrBuild<T>(delegating);
        }

    }

    public static class DelegatingModeOrBuild<T> extends DelegatingBuild<T>{

        private DelegatingModeOrBuild(Delegating<T> delegating) {
            super(delegating);
        }

        /**
         * Forces a particular delegation mode to be used.
         *
         * @param mode refer to {@link DelegationMode#DIRECT} or
         *             {@link DelegationMode#SIGNATURE} for allowed values.
         * @return the factory that will proxy instances of the supplied type.
         */
        public DelegatingBuild<T> mode(DelegationMode mode) {
            delegating.delegationMode = mode;
            return new DelegatingBuild<T>(delegating);
        }

    }

    public static class DelegatingBuild<T> {
        protected Delegating<T> delegating;

        private DelegatingBuild(Delegating<T> delegating) {
            this.delegating = delegating;
        }

        /**
         * Creating a delegating proxy for an object using a special {@link StandardProxyFactory}
         *
         * @return the created proxy implementing the <tt>type</tt>
         */
        public T build() {
            return build(new StandardProxyFactory());
        }

        /**
         * Creating a delegating proxy for an object using a special {@link ProxyFactory}
         *
         * @param factory the @{link ProxyFactory} to use.
         * @return the created proxy implementing the <tt>type</tt>
         */
        public T build(ProxyFactory factory) {
            return factory.<T>createProxy(new DelegatingInvoker<Object>(factory,
                    new SimpleReference<Object>(delegating.delegate), delegating.delegationMode), delegating.type);
        }

    }

}
