/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.failover;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.delegate.SimpleReference;
import com.thoughtworks.proxy.toys.hotswap.HotSwappingInvoker;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class FailoverInvoker extends HotSwappingInvoker {
    private final Object[] delegates;
    private final Class exceptionClass;
    private int current;

    public FailoverInvoker(Class[] types, ProxyFactory proxyFactory, Object[] delegates, Class exceptionClass) {
        super(types, proxyFactory, new SimpleReference(delegates[0]), true);
        this.delegates = delegates;
        this.exceptionClass = exceptionClass;
    }

    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            result = super.invokeMethod(proxy, method, args);
        } catch (InvocationTargetException e) {
            if (exceptionClass.isInstance(e.getTargetException())) {
                HotSwappingInvoker hiding = (HotSwappingInvoker) proxyFactory.getInvoker(proxy);
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

 }
