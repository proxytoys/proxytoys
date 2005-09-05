/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.delegate;

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
 * @since 0.1
 * @see com.thoughtworks.proxy.toys.delegate
 */
public class Delegating {
    /**
     * Delegate must implement the method's interface.
     * 
     * @deprecated since 0.2, use {@link #MODE_DIRECT}
     */
    public static final boolean STATIC_TYPING = true;
    /**
     * Delegate must have method with same name and matching signature - not necessarily the same.
     * 
     * @deprecated since 0.2, use {@link #MODE_SIGNATURE}
     */
    public static final boolean DYNAMIC_TYPING = false;

    /** Delegate must implement the method's interface */
    public static final int MODE_DIRECT = 0;
    /** Delegate must have method with same name and matching signature - not necessarily the same */
    public static final int MODE_SIGNATURE = 1;

    /**
     * Creating a delegating proxy for a signature compatible object.
     * 
     * @param type the type of the created proxy,
     * @param delegate the object the proxy delegates to.
     * @return a new proxy of the specified type.
     * @since 0.1
     */
    public static Object object(final Class type, final Object delegate) {
        return object(type, delegate, new StandardProxyFactory());
    }

    /**
     * Creating a delegating proxy for an object with a defined delegation mode.
     * 
     * @param type the type of the created proxy,
     * @param delegate the object the proxy delegates to.
     * @param delegationMode one of the delegation modes {@link #MODE_DIRECT} or {@link #MODE_SIGNATURE}
     * @return a new proxy of the specified type.
     * @throws IllegalArgumentException if the <tt>delegationMode</tt> is not one of the predefined constants
     * @since 0.2
     */
    public static Object object(final Class type, final Object delegate, final int delegationMode) {
        return object(type, delegate,new StandardProxyFactory(),delegationMode);
    }

    /**
     * Creating a delegating proxy for a signature compatible object using a special {@link ProxyFactory}.
     * 
     * @param type the type of the created proxy,
     * @param delegate the object the proxy delegates to.
     * @param factory the {@link ProxyFactory} to use creating the proxy.
     * @return a new proxy of the specified type.
     * @since 0.1
     */
    public static Object object(final Class type, final Object delegate, final ProxyFactory factory) {
        return object(type, delegate, factory, MODE_SIGNATURE);
    }

    /**
     * Creating a delegating proxy for an object with a defined delegation mode using a special {@link ProxyFactory}.
     * 
     * @param type the type of the created proxy,
     * @param delegate the object the proxy delegates to.
     * @param factory the {@link ProxyFactory} to use creating the proxy.
     * @param delegationMode one of the delegation modes {@link #MODE_DIRECT} or {@link #MODE_SIGNATURE}
     * @return a new proxy of the specified type.
     * @since 0.2.1
     */
    public static Object object(final Class type, final Object delegate, final ProxyFactory factory, final int delegationMode) {
        return factory.createProxy(new Class[]{type}, new DelegatingInvoker(
                factory, new SimpleReference(delegate), delegationMode));
    }

    /** It's a factory, stupid */
    private Delegating() {
    }
}
