/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.delegate;

import com.thoughtworks.proxy.ProxyFactory;
import static com.thoughtworks.proxy.toys.delegate.DelegationMode.*;
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
     * @param delegationMode one of the delegation modes {@link DelegationMode#DIRECT} or {@link DelegationMode#SIGNATURE}
     * @return a new proxy of the specified type.
     * @throws IllegalArgumentException if the <tt>delegationMode</tt> is not one of the predefined constants
     * @since 0.2
     */
    public static Object object(final Class type, final Object delegate, final DelegationMode delegationMode) {
        return object(type, delegate,new StandardProxyFactory(), delegationMode);
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
        return object(type, delegate, factory, SIGNATURE);
    }

    /**
     * Creating a delegating proxy for an object with a defined delegation mode using a special {@link ProxyFactory}.
     * 
     * @param type the type of the created proxy,
     * @param delegate the object the proxy delegates to.
     * @param factory the {@link ProxyFactory} to use creating the proxy.
     * @param delegationMode one of the delegation modes {@link DelegationMode#DIRECT}
     *          or {@link DelegationMode#SIGNATURE}
     * @return a new proxy of the specified type.
     * @since 0.2.1
     */
    
    public static Object object(final Class type, final Object delegate, final ProxyFactory factory, final DelegationMode delegationMode) {
        return factory.createProxy(new Class[]{type}, new DelegatingInvoker(
                factory, new SimpleReference(delegate), delegationMode));
    }

    /** It's a factory, stupid */
    private Delegating() {
    }
}
