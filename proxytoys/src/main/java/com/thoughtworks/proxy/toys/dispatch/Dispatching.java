/*
 * Created on 24-Feb-2005
 * 
 * (c) 2005 ThoughtWorks
 * 
 * See license.txt for licence details
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
 * @see com.thoughtworks.proxy.toys.dispatch
 * @since 0.2
 */
public class Dispatching {

    private Class[] types;
    private Object[] delegates;

    private Dispatching(Class[] types) {
        this.types = types;
    }

    /**
     * Creates a builder for proxy instances that allow delegation.
     *
     * @param types     the types of the proxy
     * @return a builder that will proxy instances of the supplied type.
     */
    public static DispatchingWith dispatchable(Class... types) {
        return new DispatchingWith(types);

    }

    private Object build(ProxyFactory factory) {

        final ObjectReference[] references = new ObjectReference[delegates.length];
        for (int i = 0; i < references.length; i++) {
            references[i] = new SimpleReference(delegates[i]);
        }
        return factory.createProxy(new DispatchingInvoker(factory, types, references), types);
    }

    public static class DispatchingWith {
        private final Dispatching dispatching;

        private DispatchingWith(Class[] types) {
            this.dispatching = new Dispatching(types);
        }

        /**
         * Defines the object that shall be delegated to. This delegate must implement the types used to create the hot swap or
         * have signature compatible methods.
         *
         * @param delegates the objects that will receive the calls.
         * @return the factory that will proxy instances of the supplied type.
         */
        public DispatchingBuild with(final Object... delegates) {
            dispatching.delegates = delegates;
            return new DispatchingBuild(dispatching);
        }
    }

    public static class DispatchingBuild {
        private final Dispatching dispatching;


        private DispatchingBuild(Dispatching dispatching) {

            this.dispatching = dispatching;
        }

        /**
         * * Create a dispatching proxy of given types for the given objects with {@link StandardProxyFactory}
         *
         * @return the created proxy
         */
        public Object build() {
            return build(new StandardProxyFactory());
        }

        /**
         * * Create a dispatching proxy of given types for the given objects.
         *
         * @param factory the {@link ProxyFactory} to use
         * @return the created proxy
         */
        public Object build(ProxyFactory factory) {
            return dispatching.build(factory);
        }


    }


}
