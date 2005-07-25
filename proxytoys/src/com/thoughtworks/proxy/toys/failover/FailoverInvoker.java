/*
 * Created on 14-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.failover;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.SimpleReference;
import com.thoughtworks.proxy.toys.delegate.Delegating;
import com.thoughtworks.proxy.toys.hotswap.HotSwappingInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * {@link com.thoughtworks.proxy.Invoker Invoker} that implements a failover strategy by using different delegates in case of an
 * exception. The implemented strategy is a simple round-robin algorithm to change the delegate in case of a relevant exception.
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public class FailoverInvoker extends HotSwappingInvoker {
    private final Object[] delegates;
    private final Class exceptionClass;

    private int current;
    private Object currentProxy;

    /**
     * Construct a FailoverInvoker.
     * 
     * @param types the types of the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param delegates the delegates to use
     * @param exceptionClass the type of the exception
     * @throws IllegalArgumentException if <tt>exceptionClass</tt> is not a {@link Throwable}
     */
    public FailoverInvoker(Class[] types, ProxyFactory proxyFactory, Object[] delegates, Class exceptionClass) {
        super(types, proxyFactory, new SimpleReference(delegates[0]), Delegating.STATIC_TYPING);
        if (!Throwable.class.isAssignableFrom(exceptionClass)) {
            throw new IllegalArgumentException("exceptionClass is not a Throwable");
        }
        this.delegates = delegates;
        this.exceptionClass = exceptionClass;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.currentProxy = proxy;
        return super.invoke(proxy, method, args);
    }

    protected Object invokeOnDelegate(Method method, Object[] args) throws InvocationTargetException {
        Object result = null;
        final int original = current;
        while (result == null) {
            try {
                result = super.invokeOnDelegate(method, args);
                break;
            } catch (InvocationTargetException e) {
                if (exceptionClass.isInstance(e.getTargetException())) {
                    synchronized (this) {
                        HotSwappingInvoker hiding = (HotSwappingInvoker)proxyFactory.getInvoker(currentProxy);
                        current++;
                        current = current % delegates.length;
                        if (original == current) {
                            throw e;
                        }
                        hiding.hotswap(delegates[current]);
                    }
                } else {
                    throw e;
                }
            }
        }
        return result;
    }

}
