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
 * @since 0.2
 */
public class Dispatching {
    
    public static Object object(final Class[] types, final Object[] delegates) {
        return object(types, delegates, new StandardProxyFactory());
    }
    
    public static Object object(final Class[] types, final Object[] delegates, final ProxyFactory factory) {
        final ObjectReference[] references = new ObjectReference[delegates.length];
        for (int i = 0; i < references.length; i++) {
             references[i] = new SimpleReference(delegates[i]);
        }
        return factory.createProxy(types, new DispatchingInvoker(factory, types, references));
    }
    
    /** It's a factory, stupid */
    private Dispatching(){}

}
