package com.thoughtworks.proxytoys;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates a proxy handling failover. Delegates to one object as long as there
 * is no exception, fails over to the next when an exception occurs.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class Failover extends Hiding {
    private final Object[] delegates;
    private final Class exceptionClass;
    private int current;

    public Failover(Class type, ProxyFactory proxyFactory, Object[] delegates, Class exceptionClass) {
        super(type, proxyFactory, delegates[0]);
        this.delegates = delegates;
        this.exceptionClass = exceptionClass;
    }

    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        Object result = null;
        try {
            result = super.invokeMethod(proxy, method, args);
        } catch (InvocationTargetException e) {
            if (exceptionClass.isInstance(e.getTargetException())) {
                Hiding hiding = (Hiding) proxyFactory.getInvoker(proxy);
                current++;
                current = current % delegates.length;
                hiding.hotswap(delegates[1]);
                result = super.invokeMethod(proxy, method, args);
            } else {
                throw e;
            }
        }
        return result;
    }

    public static Object object(Class type, ProxyFactory proxyFactory, Object[] delegates, Class exceptionClass) {
        return new Failover(type, proxyFactory, delegates, exceptionClass).proxy();
    }
}