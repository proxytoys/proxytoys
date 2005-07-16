/*
 * Created on 14-May-2004
 *
 * (c) 2003-2004 ThoughtWorks Ltd
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
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 */
public class FailoverInvoker extends HotSwappingInvoker {
    private final Object[] delegates;
    private final Class exceptionClass;

    private int current;
    private Object currentProxy;

    public FailoverInvoker(Class[] types, ProxyFactory proxyFactory, Object[] delegates, Class exceptionClass) {
        super(types, proxyFactory, new SimpleReference(delegates[0]), Delegating.STATIC_TYPING);
        this.delegates = delegates;
        this.exceptionClass = exceptionClass;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.currentProxy = proxy;
        return super.invoke(proxy, method, args);
    }

    protected Object invokeOnDelegate(Method method, Object[] args) throws InvocationTargetException {
        Object result = null;
        try {
            result = super.invokeOnDelegate(method, args);
        } catch (InvocationTargetException e) {
            if (exceptionClass.isInstance(e.getTargetException())) {
                synchronized (this) {
                    HotSwappingInvoker hiding = (HotSwappingInvoker)proxyFactory.getInvoker(currentProxy);
                    current++;
                    current = current % delegates.length;
                    hiding.hotswap(delegates[current]);
                    result = super.invokeOnDelegate(method, args);
                }
            } else {
                throw e;
            }
        }
        return result;
    }

}
