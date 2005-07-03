/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
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
 * Such a proxy is used to mask the methods of an object, that are not part of a public interface. Or it is used
 * to make an object compatible, e.g. when an object implements the methods of an interface, but does not
 * implement the interface itself.
 * </p>
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @since 0.1
 */
public class Delegating {
    /** Delegate must implement the method's interface */
    private static final boolean STATIC_TYPING = DelegatingInvoker.EXACT_METHOD;
    /** Delegate must have method with matching signature - not necessarily the same */
    private static final boolean DYNAMIC_TYPING = DelegatingInvoker.SAME_SIGNATURE_METHOD;
    
    /**
     * Creating a delegating proxy for a special object.
     * 
     * @param type The type of the created proxy,
     * @param delegate The object the proxy delegates to.
     * @return Returns a new proxy of the specified type.
     */
    public static Object object(Class type, Object delegate) {
        return object(type, delegate, new StandardProxyFactory());
    }
    
    /**
     * Creating a delegating proxy for a special object using a special ProxyFactory.
     * 
     * @param type The type of the created proxy,
     * @param delegate The object the proxy delegates to.
     * @param factory The {@link ProxyFactory} to use creating the proxy.
     * @return Returns a new proxy of the specified type.
     */
    public static Object object(Class type, Object delegate, ProxyFactory factory) {
        return factory.createProxy(
                new Class[] {type}, 
                new DelegatingInvoker(factory, new SimpleReference(delegate), DYNAMIC_TYPING));
    }
    
    /** It's a factory, stupid */
    private Delegating(){}
}
