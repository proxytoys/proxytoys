/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.hotswap;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.SimpleReference;


/**
 * Factory for proxy instances that allow to exchange the delegated instance. Every created proxy will implement
 * {@link Swappable}, that is used for the hot swap operation.
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @since 0.1
 * @see com.thoughtworks.proxy.toys.hotswap
 */
public class HotSwapping {

    /**
     * Create a proxy with hot swapping capability for a specific type and API compatible delegates. The delegate does not have
     * to implement the type of the proxy unless it has signature compatible methods.
     * 
     * @param type the type of the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param delegate the delegated object
     * @return the created proxy implementing the <tt>type</tt> and {@link Swappable}
     * @since 0.1
     */
    public static Object object(final Class type, final ProxyFactory proxyFactory, final Object delegate) {
        return object(new Class[]{type}, proxyFactory, delegate, type.isInstance(delegate));
    }

    /**
     * Create a proxy with hot swapping capabilities for specifiy types of the delegate. The delegate must implement the given
     * types, if the invoker is in static typing mode, otherwise it must only have signature compatible methods.
     * 
     * @param types the types of the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param delegate the delegated object
     * @param staticTyping {@link com.thoughtworks.proxy.toys.delegate.Delegating#STATIC_TYPING STATIC_TYPING} or
     *            {@link com.thoughtworks.proxy.toys.delegate.Delegating#DYNAMIC_TYPING DYNAMIC_TYPING}
     * @return the created proxy implementing the <tt>types</tt> and {@link Swappable}
     * @since 0.1
     */
    public static Object object(
            final Class[] types, final ProxyFactory proxyFactory, final Object delegate, final boolean staticTyping) {
        final ObjectReference delegateReference = new SimpleReference(delegate);
        return object(types, proxyFactory, delegateReference, staticTyping);
    }

    /**
     * Create a proxy with hot swapping capabilities for specifiy types of the delegate given with an {@link ObjectReference}.
     * The delegate must implement the given types, if the invoker is in static typing mode, otherwise it must only have
     * signature compatible methods.
     * 
     * @param types the types of the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param objectReference the {@link ObjectReference} with the delegate
     * @param staticTyping {@link com.thoughtworks.proxy.toys.delegate.Delegating#STATIC_TYPING STATIC_TYPING} or
     *            {@link com.thoughtworks.proxy.toys.delegate.Delegating#DYNAMIC_TYPING DYNAMIC_TYPING}
     * @return the created proxy implementing the <tt>types</tt> and {@link Swappable}
     * @since 0.1
     */
    public static Object object(
            final Class[] types, final ProxyFactory proxyFactory, final ObjectReference objectReference, final boolean staticTyping) {
        return new HotSwappingInvoker(types, proxyFactory, objectReference, staticTyping).proxy();
    }

    /** It's a factory, stupid */
    private HotSwapping() {
    }
}
