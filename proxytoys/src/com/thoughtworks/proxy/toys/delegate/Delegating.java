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
 */
public class Delegating {
    /** delegate must implement the method's interface */
    public static final boolean STATIC_TYPING = DelegatingInvoker.STATIC_TYPING;
    /** delegate must have method with matching signature - not necessarily the same */
    public static final boolean DYNAMIC_TYPING = DelegatingInvoker.DYNAMIC_TYPING;
    
    public static Object object(Class type, Object delegate) {
        return object(type, delegate, new StandardProxyFactory());
    }
    
    public static Object object(Class type, Object delegate, ProxyFactory factory) {
        return factory.createProxy(new Class[] {type}, new DelegatingInvoker(delegate));
    }
    
    /** It's a factory, stupid */
    private Delegating(){}
}
