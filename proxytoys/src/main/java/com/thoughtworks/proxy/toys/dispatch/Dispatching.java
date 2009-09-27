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


    private Dispatching(Class[] types, Object[] delegates) {
        this.types = types;
        this.delegates = delegates;
    }

    /**
     * Creates a factory for proxy instances that allow delegation.
     *
     * @param types     the types of the proxy
     * @param delegates the objects, that will receive the calls
     * @return a factory that will proxy instances of the supplied type.
     * @since 0.2
     */

    public static  Dispatching dispatchable(Class[] types, Object[] delegates) {
        return new Dispatching(types, delegates);

    }

    /**
     * * Create a dispatching proxy of given types for the given objects with {@link StandardProxyFactory}
     *
     * @return the created proxy
     * @since 0.2
     */
    public Object build() {
        return build(new StandardProxyFactory());
    }

    /**
     * * Create a dispatching proxy of given types for the given objects.
     *
     * @param factory the {@link ProxyFactory} to use
     * @return the created proxy
     * @since 0.2
     */
    public Object build(ProxyFactory factory) {

        final ObjectReference[] references = new ObjectReference[delegates.length];
        for (int i = 0; i < references.length; i++) {
            references[i] = new SimpleReference(delegates[i]);
        }
        return factory.createProxy(types, new DispatchingInvoker(factory, types, references));
    }
}
