/*
 * Created on 14-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.multicast;

import java.util.Arrays;
import java.util.Set;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;

/**
 * Toy factory to create proxies delegating a call to multiple objects and managing the individual results.
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.proxy.toys.multicast
 */
public class Multicasting<T> {
    private Class<?>[] types;
    private Object[] delegates;

    public Multicasting(Object... delegates) {
        this.delegates = delegates;
    }

    public Multicasting(Class<?> primaryType, Class<?>... types) {
        this.types = makeTypesArray(primaryType, types);
    }

    /**
     * Creates a factory for proxy instances delegating a call to multiple objects and managing the individual results.
     *
     * @param primaryType the primary type implemented by the proxy
     * @param types other types that are implemented by the proxy
     * @return a factory that will proxy instances of the supplied type.
     */
    public static <T> MulticastingWith<T> proxy(Class<T> primaryType, Class<?>... types) {
        return new MulticastingWith<T>(primaryType, types);
    }

    /**
     * Creates a factory for proxy instances delegating a call to multiple objects and managing the individual results.
     *
     * @param targets targets the target objects
     * @return a factory that will proxy instances of the supplied type.
     */
    public static MulticastingBuild<Multicast> proxy(Object... targets) {
        return new MulticastingBuild<Multicast>(targets);
    }

    public static class MulticastingWith<T> {
        Multicasting<T> multicasting;

        private MulticastingWith(Class<T> primaryType, Class<?>[] types) {
            multicasting = new Multicasting<T>(primaryType, types);
        }

        /**
         * With these target Objects
         * @param targets targets the target objects
         * @return the factory
         */
        public MulticastingBuild<T> with(Object... targets) {
            multicasting.delegates = targets;
            return new MulticastingBuild<T>(multicasting);
        }
    }

    public static class MulticastingBuild<T> {
        private final Multicasting<T> multicasting;

        private MulticastingBuild(Object[] targets) {
            multicasting = new Multicasting<T>(targets);
        }

        private MulticastingBuild(Multicasting<T> multicasting) {
            this.multicasting = multicasting;
        }

        /**
         * @return the proxy using StandardProxyFactory
         */
        public T build() {
            return multicasting.build();
        }

        /**
         * Generate a proxy for the specified types calling the methods on the given targets using the {@link StandardProxyFactory}.
         * <p>
         * Note, that the method will only return a proxy if necessary. If there is only one target instance and this
         * instance implements all of the specified types, then there is no point in creating a proxy.
         * </p>
         *
         * @param factory the factory used to generate the proxy
         * @return the new proxy implementing {@link Multicast} or the only target
         */
        public T build(ProxyFactory factory) {
            return multicasting.build(factory);
        }

    }

    /**
     * @return the proxy using StandardProxyFactory
     */
    public T build() {
        return build(new StandardProxyFactory());
    }

    /**
     * Generate a proxy for the specified types calling the methods on the given targets using a special {@link ProxyFactory}.
     * <p>
     * Note, that the method will only return a proxy if necessary. If there is only one target instance and this
     * instance implements all of the specified types, then there is no point in creating a proxy.
     * </p>
     *
     * @param factory the factory used to generate the proxy
     * @return the new proxy implementing {@link Multicast} or the only target
     */
    public T build(ProxyFactory factory) {
        if (types == null) {
            return buildWithNoTypesInput(factory);
        }

        if (delegates.length == 1) {
            int i;
            for (i = 0; i < types.length; i++) {
                if (types[i] == Multicast.class) {
                    continue;
                }
                if (!types[i].isAssignableFrom(delegates[0].getClass())) {
                    break;
                }
            }
            if (i == types.length) {
                @SuppressWarnings("unchecked")
                final T instance = (T) delegates[0];
                return instance;
            }
        }
        return new MulticastingInvoker<T>(types, factory, delegates).proxy();
    }

    private T buildWithNoTypesInput(ProxyFactory factory) {
        if (delegates.length > 1) {
            final Class<?> superclass = ReflectionUtils.getMostCommonSuperclass(delegates);
            final Set<Class<?>> interfaces = ReflectionUtils.getAllInterfaces(delegates);
            ReflectionUtils.addIfClassProxyingSupportedAndNotObject(superclass, interfaces, factory);
            this.types = interfaces.toArray(new Class<?>[interfaces.size()]);
            return new MulticastingInvoker<T>(types, factory, delegates).proxy();
        }
        @SuppressWarnings("unchecked")
        final T instance = (T) delegates[0];
        return instance;
    }

    private Class<?>[] makeTypesArray(Class<?> primaryType, Class<?>[] types) {
        Class<?>[] retVal = new Class[types.length +1];
        retVal[0] = primaryType;
        System.arraycopy(types, 0, retVal, 1, types.length);
        return retVal;
    }
}
