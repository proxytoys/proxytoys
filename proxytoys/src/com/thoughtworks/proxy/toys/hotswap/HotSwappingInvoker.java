/*
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.hotswap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospector;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 */
public class HotSwappingInvoker implements Invoker {
    private static final Method hotswap;

    static {
        try {
            hotswap = Swappable.class.getMethod("hotswap", new Class[]{Object.class});
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    protected final ProxyFactory proxyFactory;
    private final Class[] types;
    private final ObjectReference delegateReference;
    private boolean executing = false;

    public HotSwappingInvoker(Class[] types, ProxyFactory proxyFactory, ObjectReference delegateReference) {
        this.proxyFactory = proxyFactory;
        this.types = types;
        this.delegateReference = delegateReference;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (method.equals(ClassHierarchyIntrospector.equals)) {
            Object arg = args[0];
            if (proxyFactory.isProxyClass(arg.getClass())) {
                arg = proxyFactory.getInvoker(arg);
            }
            result = new Boolean(equals(arg));
        } else if (method.equals(ClassHierarchyIntrospector.hashCode)) {
            result = new Integer(hashCode());
        } else if (method.equals(hotswap)) {
            result = hotswap(args[0]);
        } else {
            if (executing) {
                throw new IllegalStateException("Cyclic dependency");
            }
            executing = true;

            result = invokeMethod(proxy, method, args);
            if (result != null && proxyFactory.canProxy(result.getClass())) {
                result = HotSwapping.object(result.getClass(), proxyFactory, result);
            }
        }
        executing = false;
        return result;
    }

    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        Object delegate = delegateReference.get();
        return method.invoke(delegate, args);
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
