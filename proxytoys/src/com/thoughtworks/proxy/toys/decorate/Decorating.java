/*
 * Created on 03-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

// TODO: use the AOP alliance API

/**
 * Toy factory to create proxies decorating an object in an AOP style.
 * <p>
 * An InvocationDecorator is used for the additional functionality. It is called before the original method is called,
 * after the original method was called, after the original method has thrown an exceptionor when an exception occurs,
 * calling the method of the decorated object.
 * </p>
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public class Decorating {
	/**
	 * Create a decorating proxy implementing a specific type.
	 * 
	 * @param type The type of the created proxy.
	 * @param delegate The decorated object.
	 * @param decorator The decorator instance.
	 * @return Returns a decorating proxy.
	 */
	public static Object object(Class type, Object delegate, InvocationDecorator decorator) {
        return object(new Class[] {type}, delegate, decorator);
	}

    /**
     * Create a decorating proxy implementing specific types.
     * 
     * @param types The types of the created proxy.
     * @param delegate The decorated object.
     * @param decorator The decorator instance.
     * @return Returns a decorating proxy.
     */
	public static Object object(Class[] types, Object delegate, InvocationDecorator decorator) {
        return object(types, delegate, decorator, new StandardProxyFactory());
	}


    /**
     * Create a decorating proxy implementing specific types using a provided {@link ProxyFactory}.
     * 
     * @param types The types of the created proxy.
     * @param delegate The decorated object.
     * @param decorator The decorator instance.
     * @param factory The ProxyFactory to use for the proxy generation.
     * @return Returns a decorating proxy.
     */
	public static Object object(Class[] types, Object delegate, InvocationDecorator decorator, ProxyFactory factory) {
        return factory.createProxy(types, new DecoratingInvoker(delegate, decorator));
	}

    /** It's a factory, stupid */
    private Decorating(){}
}
