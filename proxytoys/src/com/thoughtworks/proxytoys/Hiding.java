/*
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxytoys;

import java.lang.reflect.Method;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 */
public class Hiding implements Invoker {
    private static final Method hotswap;

    static {
        try {
            hotswap = Swappable.class.getMethod("hotswap", new Class[]{Object.class});
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    private final ProxyFactory proxyFactory;
    private final Class type;
    private Object delegate;
    private boolean executing = false;

    public Hiding(Class type, ProxyFactory proxyFactory, Object delegate) {
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
            result = method.invoke(delegate, args);
            if(proxyFactory.canProxy(result.getClass())) {
                result = object(result.getClass(), proxyFactory, result);
            }
        }
        executing = false;
        return result;
    }

    private Object hotswap(Object newDelegate) {
        Object result = delegate;
        delegate = newDelegate;
        return result;
    }

    public Object proxy() {
        return proxyFactory.createProxy(new Class[]{type, Swappable.class}, this);
    }

    /**
     * @return a proxy that hides the implementation and implements {@link Swappable}.
     */
    public static Object object(Class type, ProxyFactory proxyFactory, Object delegate) {
        return new Hiding(type, proxyFactory, delegate).proxy();
    }

}
