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
import com.thoughtworks.proxy.kit.ClassHierarchyIntrospector;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class Multicasting {

	public static Object object(Class[] types, ProxyFactory proxyFactory, Object[] targets) {
        if (targets.length == 1) {
            int i;
            for (i = 0; i < types.length; i++) {
                if (!types[i].isAssignableFrom(targets[0].getClass())) {
                    break;
                }
            }
            if (i == types.length) {
                return targets[0];
            }
        }
	    return new MulticastingInvoker(types, proxyFactory, targets).proxy();
	}

	public static Object object(Class type, ProxyFactory proxyFactory, Object[] targets) {
	    return object(new Class[]{type}, proxyFactory, targets);
	}

	public static Object object(ProxyFactory proxyFactory, Object[] targets) {
        if (targets.length > 1) {
    	    Class superclass = ClassHierarchyIntrospector.getMostCommonSuperclass(targets);
    	    Class[] interfaces = ClassHierarchyIntrospector.getAllInterfaces(targets);
    	    Class[] proxyTypes = ClassHierarchyIntrospector.addIfClassProxyingSupportedAndNotObject(superclass, interfaces, proxyFactory);
    	    return object(proxyTypes, proxyFactory, targets);
        } else {
            return targets[0];
        }
	}

	public static Object object(Object[] targets) {
	    return object(new StandardProxyFactory(), targets);
	}

    /** It's a factory, stupid */
    private Multicasting(){}
}
