/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxytoys.delegate;

import com.thoughtworks.proxytoys.ProxyFactory;
import com.thoughtworks.proxytoys.StandardProxyFactory;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class DelegatingProxy {
	private static ProxyFactory factory = new StandardProxyFactory();
    
    public static Object newProxyInstance(Class type, Object delegate) {
        return factory.createProxy(new Class[] {type}, new DelegatingInvoker(delegate));
    }
}
