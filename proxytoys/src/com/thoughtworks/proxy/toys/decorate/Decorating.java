/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class Decorating {
	public static Object object(Class type, Object delegate, InvocationDecorator decorator) {
        return object(type, delegate, decorator, new StandardProxyFactory());
	}
    
	public static Object object(Class type, Object delegate, InvocationDecorator decorator, ProxyFactory factory) {
        return factory.createProxy(new Class[] {type},
                new DecoratingInvoker(delegate, decorator));
	}
    
    /** It's a factory, stupid */
    private Decorating(){}
}
