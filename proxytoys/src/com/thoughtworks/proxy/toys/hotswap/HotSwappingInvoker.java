/*
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.hotswap;

import java.lang.reflect.Method;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.delegate.*;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 */
public class HotSwappingInvoker extends DelegatingInvoker {
    private static final Method hotswap;

    static {
        try {
            hotswap = Swappable.class.getMethod("hotswap", new Class[]{Object.class});
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    private final Class[] types;
    
    public HotSwappingInvoker(Class[] types, ProxyFactory proxyFactory, ObjectReference delegateReference, boolean isTypeForgiving) {
        super(proxyFactory, delegateReference, isTypeForgiving);
        this.types = types;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (method.equals(hotswap)) {
            result = hotswap(args[0]);
        } else
			result = super.invoke(proxy, method, args);
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
