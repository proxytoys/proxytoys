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

// TODO: use the AOP alliance API

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 */
public class Decorating {
	public static Object object(Class type, Object delegate, InvocationDecorator decorator) {
        return object(new Class[] {type}, delegate, decorator);
	}

	public static Object object(Class[] types, Object delegate, InvocationDecorator decorator) {
        return object(types, delegate, decorator, new StandardProxyFactory());
	}

	public static Object object(Class[] types, Object delegate, InvocationDecorator decorator, ProxyFactory factory) {
        return factory.createProxy(types, new DecoratingInvoker(delegate, decorator));
	}

    /** It's a factory, stupid */
    private Decorating(){}
}
