/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24-Feb-2005
 */
package com.thoughtworks.proxy.toys.dispatch;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.SimpleReference;

/**
 * Proxy factory for dispatching proxy instances.
 *
 * @author J&ouml;rg Schaible
 * @author Juan Li
 * @author Paul Hammant
 * @see com.thoughtworks.proxy.toys.dispatch
 * @since 0.2
 */
public class Dispatching<T> {

    private Class<?>[] types;
    private Object[] delegates;

    private Dispatching(Class<?>[] types) {
        this.types = types;
    }

    /**
     * Creates a builder for proxy instances that allow delegation.
     *
     * @param primaryType the primary type of the proxy that will not have to be cast to
     * @param types the other types of the proxy
     * @return a builder that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <T> DispatchingWith<T> proxy(Class<T> primaryType, Class<?>... types) {
        return new DispatchingWith<T>(primaryType,  types);
    }

    private T build(ProxyFactory factory) {
        @SuppressWarnings("unchecked")
        final ObjectReference<Object>[] references = new ObjectReference[delegates.length];
        for (int i = 0; i < references.length; i++) {
            references[i] = new SimpleReference<Object>(delegates[i]);
        }
        return factory.<T>createProxy(new DispatchingInvoker(factory, types, references), types);
    }

    public static class DispatchingWith<T> {
        private final Dispatching<T> dispatching;

        private DispatchingWith(Class<T> primaryType, Class<?>[] types) {
            this.dispatching = new Dispatching<T>(makeTypesArray(primaryType, types));
        }

        private Class<?>[] makeTypesArray(Class<T> primaryType, Class<?>[] types) {
            Class<?>[] retVal = new Class[types.length +1];
            retVal[0] = primaryType;
            System.arraycopy(types, 0, retVal, 1, types.length);
            return retVal;
        }

        /**
         * Defines the object that shall be delegated to. This delegate must implement the types used to create the
         * dispatching proxy or have signature compatible methods.
         *
         * @param delegates the objects that will receive the calls.
         * @return the factory that will proxy instances of the supplied type.
         * @since 1.0
         */
        public DispatchingBuild<T> with(final Object... delegates) {
            dispatching.delegates = delegates;
            return new DispatchingBuild<T>(dispatching);
        }
    }

    public static class DispatchingBuild<T> {
        private final Dispatching<T> dispatching;

        private DispatchingBuild(Dispatching<T> dispatching) {
            this.dispatching = dispatching;
        }

        /**
         * Create a dispatching proxy of given types for the given objects using the {@link StandardProxyFactory}
         *
         * @return the created proxy
         * @since 1.0
         */
        public T build() {
            return build(new StandardProxyFactory());
        }

        /**
         * Create a dispatching proxy of given types for the given objects using a special {@link ProxyFactory}.
         *
         * @param factory the {@link ProxyFactory} to use
         * @return the created proxy
         * @since 1.0
         */
        public T build(ProxyFactory factory) {
            return dispatching.build(factory);
        }
    }
}
