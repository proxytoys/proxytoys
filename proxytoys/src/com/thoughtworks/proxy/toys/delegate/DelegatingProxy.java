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
public class DelegatingProxy {
	private static ProxyFactory factory = new StandardProxyFactory();
    
    public static Object newProxyInstance(Class type, Object delegate) {
        return factory.createProxy(new Class[] {type}, new DelegatingInvoker(delegate));
    }
}
