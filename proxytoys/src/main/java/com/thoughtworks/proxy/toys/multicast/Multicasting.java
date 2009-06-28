/*
 * Created on 14-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;

import java.util.Set;


/**
 * Toy factory to create proxies delegating a call to multiple objects and managing the individual results.
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @since 0.1
 * @see com.thoughtworks.proxy.toys.multicast
 */
public class Multicasting {

    /**
     * Generate a proxy for the specified types calling the methods on the given targets.
     * <p>
     * Note, that the method will only return a proxy if necessary. If there is only one target instance and this
     * instance implements all of the specified types, then there is no point in creating a proxy.
     * </p>
     * 
     * @param types the types that are implemented by the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param targets the target objects
     * @return the new proxy implementing {@link Multicast} or the only target
     * @since 0.1
     */
    public static Object object(final Class[] types, final ProxyFactory proxyFactory, final Object[] targets) {
        if (targets.length == 1) {
            int i;
            for (i = 0; i < types.length; i++) {
                if (types[i] == Multicast.class) {
                    continue;
                }
                if (!types[i].isAssignableFrom(targets[0].getClass())) {
                    break;
                }
            }
            if (i == types.length) {
                return targets[0];
            }
        }
        return new MulticastingInvoker(types, proxyFactory, targets).proxy();
    }

    /**
     * Generate a proxy for the specified type calling the methods on the given targets.
     * <p>
     * Note, that the method will only return a proxy if necessary. If there is only one target instance and this
     * instance implements the specified type, then there is no point in creating a proxy.
     * </p>
     * 
     * @param type the type that is implemented by the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param targets the target objects
     * @return the new proxy implementing {@link Multicast} or the only target
     * @since 0.1
     */
    public static Object object(final Class type, final ProxyFactory proxyFactory, final Object[] targets) {
        return object(new Class[]{type}, proxyFactory, targets);
    }

    /**
     * Generate a proxy that is calling the methods on the given targets.
     * <p>
     * The type of the proxy is a combination of all interfaces implemented by all targets and their most common super
     * class (if supported by the {@link ProxyFactory}). Note, that the method will only return a proxy if necessary.
     * If there is only one target instance, then there is no point in creating a proxy.
     * </p>
     * 
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param targets the target objects
     * @return the new proxy implementing {@link Multicast} or the only target
     * @since 0.1
     */
    public static Object object(final ProxyFactory proxyFactory, final Object[] targets) {
        if (targets.length > 1) {
            final Class superclass = ReflectionUtils.getMostCommonSuperclass(targets);
            final Set interfaces = ReflectionUtils.getAllInterfaces(targets);
            ReflectionUtils.addIfClassProxyingSupportedAndNotObject(superclass, interfaces, proxyFactory);
            return object(ReflectionUtils.toClassArray(interfaces), proxyFactory, targets);
        } else {
            return targets[0];
        }
    }

    /**
     * Generate a proxy that is calling the methods on the given targets using a {@link StandardProxyFactory}.
     * <p>
     * The type of the proxy is a combination of all interfaces implemented by all targets. Note, that the method will
     * only return a proxy if necessary. If there is only one target instance, then there is no point in creating a
     * proxy.
     * </p>
     * 
     * @param targets the target objects
     * @return the new proxy implementing {@link Multicast} or the only target
     * @since 0.1
     */
    public static Object object(final Object[] targets) {
        return object(new StandardProxyFactory(), targets);
    }

    /** It's a factory, stupid */
    private Multicasting() {
    }
}
