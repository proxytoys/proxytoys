/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.hotswap;

import static com.thoughtworks.proxy.toys.delegate.DelegationMode.DIRECT;
import static com.thoughtworks.proxy.toys.delegate.DelegationMode.SIGNATURE;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.SimpleReference;
import com.thoughtworks.proxy.toys.delegate.DelegationMode;


/**
 * Factory for proxy instances that allow to exchange the delegated instance. Every created proxy will implement
 * {@link Swappable}, that is used for the hot swap operation.
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @see com.thoughtworks.proxy.toys.hotswap
 */
public class HotSwapping<T> {

    private Object instance;
    private Class<T> type;
    private DelegationMode delegationMode;

    private HotSwapping(final Class<T> types) {
        this.type = types;
    }

    /**
     * Creates a factory for proxy instances that allow the exchange of delegated instances.
     *
     * @param type the type of the proxy when it is finally created.
     * @return a factory that will proxy instances of the supplied type.
     */
    public static <T> HotSwappingWith<T> hotSwappable(final Class<T> type) {
        return new HotSwappingWith<T>(type);
    }

    /**
     * Create a proxy with hot swapping capabilities for specific types of the delegate given with an
     * {@link ObjectReference}. The delegate must implement the given types, if the invoker is in static typing mode,
     * otherwise it must only have signature compatible methods. Proxies created by this method will implement
     * {@link Swappable}
     *
     * @param factory the {@link ProxyFactory} to use.
     * @return the created proxy implementing the <tt>types</tt> and {@link Swappable}
     */
    private T build(final ProxyFactory factory) {
        final ObjectReference<Object> delegateReference = new SimpleReference<Object>(instance);
        return new HotSwappingInvoker<T>(new Class[]{type}, factory, delegateReference, delegationMode).proxy();
    }

    public static class HotSwappingWith<T> {
        private final HotSwapping<T> hotswapping;

        public HotSwappingWith(Class<T> type) {
            this.hotswapping = new HotSwapping<T>(type);
        }

        /**
         * Defines the object that shall be proxied. This delegate must implement the types used to create the hot swap or
         * have signature compatible methods.
         *
         * @param instance the object that shall be proxied.
         * @return the factory that will proxy instances of the supplied type.
         */
        public HotSwappingBuildOrMode<T> with(final Object instance) {
            hotswapping.instance = instance;
            hotswapping.delegationMode = hotswapping.type.isInstance(instance) ? DIRECT : SIGNATURE;
            return new HotSwappingBuildOrMode<T>(hotswapping);
        }
    }

    public static class HotSwappingBuildOrMode<T> extends HotSwappingBuild<T>{
        public HotSwappingBuildOrMode(HotSwapping<T> hotswapping) {
            super(hotswapping);
        }

        /**
         * Forces a particular delegation mode to be used.
         *
         * @param delegationMode refer to {@link DelegationMode#DIRECT} or
         *                       {@link DelegationMode#SIGNATURE} for allowed
         *                       values.
         * @return the factory that will proxy instances of the supplied type.
         */
        public HotSwappingBuild<T> mode(DelegationMode delegationMode) {
            hotswapping.delegationMode = delegationMode;
            return new HotSwappingBuild<T>(hotswapping);
        }
    }

    public static class HotSwappingBuild<T> {
        protected final HotSwapping<T> hotswapping;

        public HotSwappingBuild(HotSwapping<T> hotswapping) {
            this.hotswapping = hotswapping;
        }
        
        /**
         * Create a proxy with hot swapping capabilities for specific types of the delegate given with an
         * {@link ObjectReference}. The delegate must implement the given types, if the invoker is in static typing mode,
         * otherwise it must only have signature compatible methods. Proxies created by this method will implement
         * {@link Swappable}
         *
         * @param factory the {@link ProxyFactory} to use.
         * @return the created proxy implementing the <tt>types</tt> and {@link Swappable}
         */
        public T build(final ProxyFactory factory) {
            return hotswapping.build(factory);
        }
    }
}
