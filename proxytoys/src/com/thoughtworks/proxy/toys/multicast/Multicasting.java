/*
 * Created on 14-May-2004
 *
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 */
public class Multicasting {

	public static Object object(Class[] types, ProxyFactory proxyFactory, Object[] targets) {
	    return new MulticastingInvoker(types, proxyFactory, targets).proxy();
	}

	public static Object object(Class type, ProxyFactory proxyFactory, Object[] targets) {
	    return object(new Class[]{type}, proxyFactory, targets);
	}

	public static Object object(ProxyFactory proxyFactory, Object[] targets) {
	    Class superclass = ClassHierarchyIntrospector.getMostCommonSuperclass(targets);
	    Class[] interfaces = ClassHierarchyIntrospector.getAllInterfaces(targets);
	    Class[] proxyTypes = ClassHierarchyIntrospector.addIfClassProxyingSupportedAndNotObject(superclass, interfaces, proxyFactory);
	    return object(proxyTypes, proxyFactory, targets);
	}

	public static Object object(Object[] targets) {
	    return object(new StandardProxyFactory(), targets);
	}

    /** It's a factory, stupid */
    private Multicasting(){}
}
