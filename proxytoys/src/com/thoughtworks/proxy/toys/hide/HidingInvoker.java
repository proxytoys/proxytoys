/*
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.hide;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 */
public class HidingInvoker implements Invoker {
    private static final Method hotswap;

    static {
        try {
            hotswap = Swappable.class.getMethod("hotswap", new Class[]{Object.class});
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    protected final ProxyFactory proxyFactory;
    private final Class type;
    private Object delegate;
    private boolean executing = false;

    public HidingInvoker(Class type, ProxyFactory proxyFactory, Object delegate) {
        this.proxyFactory = proxyFactory;
        this.type = type;
        this.delegate = delegate;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(executing) {
            throw new IllegalStateException("Cyclic dependency");
        }
        executing = true;

        Object result;
        if(method.equals(hotswap)) {
            result = hotswap(args[0]);
        } else {
            result = invokeMethod(proxy,  method, args);
            if(result != null && proxyFactory.canProxy(result.getClass())) {
                result = Hiding.object(result.getClass(), proxyFactory, result);
            }
        }
        executing = false;
        return result;
    }

    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        return method.invoke(delegate, args);
    }

    public Object hotswap(Object newDelegate) {
        Object result = delegate;
        delegate = newDelegate;
        return result;
    }

    public Object proxy() {
        return proxyFactory.createProxy(new Class[]{type, Swappable.class}, this);
    }
}
