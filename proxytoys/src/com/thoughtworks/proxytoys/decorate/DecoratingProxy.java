/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxytoys.decorate;

import com.thoughtworks.proxytoys.ProxyFactory;
import com.thoughtworks.proxytoys.StandardProxyFactory;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class DecoratingProxy {
    private static ProxyFactory factory = new StandardProxyFactory();
    
	public static Object newProxyInstance(Class type, Object delegate, InvocationDecorator interceptor) {
        return factory.createProxy(new Class[] {type},
                new DecoratingInvoker(delegate, interceptor));
	}
}
