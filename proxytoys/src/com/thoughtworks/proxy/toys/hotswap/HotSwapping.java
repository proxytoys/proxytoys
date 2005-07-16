/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.hotswap;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.SimpleReference;


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
    public static Object object(Class[] types, ProxyFactory proxyFactory, Object delegate, boolean staticTyping) {
        ObjectReference delegateReference = new SimpleReference(delegate);
        return object(types, proxyFactory, delegateReference, staticTyping);
    }

    public static Object object(Class[] types, ProxyFactory proxyFactory, ObjectReference objectReference, boolean staticTyping) {
        return new HotSwappingInvoker(types, proxyFactory, objectReference, staticTyping).proxy();
    }

    /** It's a factory, stupid */
    private HotSwapping() {
    }
}
