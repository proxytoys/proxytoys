/*
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.hotswap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker2;
import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospector;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 */
public class HotSwappingInvoker extends DelegatingInvoker2 {
    private static final Method hotswap;

    static {
        try {
            hotswap = Swappable.class.getMethod("hotswap", new Class[]{Object.class});
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    public HotSwappingInvoker(Class[] types, ProxyFactory proxyFactory, ObjectReference delegateReference, boolean isTypeForgiving) {
        super(types, proxyFactory, delegateReference, isTypeForgiving);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (method.equals(hotswap)) {
            result = hotswap(args[0]);
        } else
			result = doInvoke(proxy, method, args);
        return result;
    }

    public Object hotswap(Object newDelegate) {
        Object result = delegateReference.get();
        delegateReference.set(newDelegate);
        return result;
    }

    public Object proxy() {
        Class[] typesWithSwappable = new Class[types.length + 1];
        System.arraycopy(types, 0, typesWithSwappable, 0, types.length);
        typesWithSwappable[types.length] = Swappable.class;
        return proxyFactory.createProxy(typesWithSwappable, this);
    }
}
