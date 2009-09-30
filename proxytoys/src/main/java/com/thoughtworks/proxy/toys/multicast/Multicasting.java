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
 * @see com.thoughtworks.proxy.toys.multicast
 * @since 0.1
 */
public class Multicasting<T> {
    private Class[] types;
    private Object[] delegates;

    public Multicasting(Object... delegates) {
        this.delegates = delegates;

    }

    /**
     * Creates a factory for proxy instances delegating a call to multiple objects and managing the individual results.
     *
     * @param targets targets the target objects
     * @return a factory that will proxy instances of the supplied type.
     * @since 0.2
     */

    public static <T> Multicasting<T> multicastable(Object... targets) {
        return new Multicasting<T>(targets);
    }

    /**
     * @param types the types that are implemented by the proxy
     * @return the factory
     */
    public Multicasting<T> withTypes(Class... types) {
        this.types = types;
        return this;
    }

    /**
     * @return the proxy using StandardProxyFactory
     */
    public T build() {
        return build(new StandardProxyFactory());
    }

    /**
     * Generate a proxy for the specified types calling the methods on the given targets.
     * <p>
     * Note, that the method will only return a proxy if necessary. If there is only one target instance and this
     * instance implements all of the specified types, then there is no point in creating a proxy.
     * </p>
     *
     * @param factory the factory used to generate the proxy
     * @return the new proxy implementing {@link Multicast} or the only target
     * @since 0.1
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
                return (T) delegates[0];
            }
        }
        return (T) new MulticastingInvoker(types, factory, delegates).proxy();
    }

    private T buildWithNoTypesInput(ProxyFactory factory) {
        if (delegates.length > 1) {
            final Class superclass = ReflectionUtils.getMostCommonSuperclass(delegates);
            final Set interfaces = ReflectionUtils.getAllInterfaces(delegates);
            ReflectionUtils.addIfClassProxyingSupportedAndNotObject(superclass, interfaces, factory);
            this.types = ReflectionUtils.toClassArray(interfaces);
            return (T) new MulticastingInvoker(types, factory, delegates).proxy();
        }
        return (T) delegates[0];
    }

}
