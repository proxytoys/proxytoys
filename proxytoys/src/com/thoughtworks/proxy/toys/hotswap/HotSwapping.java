/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.hotswap;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.delegate.*;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class HotSwapping {

    public static Object object(Class type, ProxyFactory proxyFactory, Object delegate) {
        return object(new Class[]{type}, proxyFactory, delegate, type.isInstance(delegate));
    }

    /**
     * @return a proxy that hides the implementation and implements {@link Swappable}.
     */
    public static Object object(Class[] types, ProxyFactory proxyFactory, Object delegate, boolean forceSameType) {
        ObjectReference delegateReference = new SimpleReference(delegate);
        return object(types, proxyFactory, delegateReference, forceSameType);
    }

	public static Object object(Class[] types, ProxyFactory proxyFactory, ObjectReference objectReference, boolean forceSameType) {
	    return new HotSwappingInvoker(types, proxyFactory, objectReference, forceSameType).proxy();
	}
    
    /** It's a factory, stupid */
    private HotSwapping(){}
}
