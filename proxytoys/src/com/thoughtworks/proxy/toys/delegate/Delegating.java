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

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @deprecated Use {@link com.thoughtworks.proxy.toys.hotswap.HotSwapping}
 */
public class Delegating {
    
    public static Object object(Class type, Object delegate) {
        return object(type, delegate, new StandardProxyFactory());
    }
    
    public static Object object(Class type, Object delegate, ProxyFactory factory) {
        return factory.createProxy(new Class[] {type}, new OldDelegatingInvoker(delegate));
    }
    
    /** It's a factory, stupid */
    private Delegating(){}
}
