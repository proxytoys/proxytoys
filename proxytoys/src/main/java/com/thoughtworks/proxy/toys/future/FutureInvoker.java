/*
 * (c) 2003-2004, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29-May-2004
 */
package com.thoughtworks.proxy.toys.future;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;
import com.thoughtworks.proxy.toys.hotswap.Swappable;
import com.thoughtworks.proxy.toys.nullobject.Null;

/**
 * @author Aslak Helles&oslash;y
 * @since 1.0
 */
// TODO: Javadoc
public class FutureInvoker implements Invoker {
    private static final long serialVersionUID = 1L;
    private final Object target;
    private final ProxyFactory proxyFactory;
    private final ExecutorService executor;

    public FutureInvoker(Object target, ProxyFactory proxyFactory, ExecutorService executor) {
        this.target = target;
        this.proxyFactory = proxyFactory;
        this.executor = executor;
    }

    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        Object result = null;
        if (!returnType.equals(void.class)) {
            Object nullResult = Null.proxy(returnType).build(proxyFactory);
            final Swappable swappableResult = Swappable.class.cast(HotSwapping.proxy(returnType).with(nullResult).build(proxyFactory));
            result = swappableResult;
            final Callable<Swappable> callable = new Callable<Swappable>() {
                public Swappable call() throws IllegalAccessException, InvocationTargetException {
                    Object invocationResult = method.invoke(target, args);
                    swappableResult.hotswap(invocationResult);
                    return swappableResult;
                }
            };
            executor.submit(callable);
        }

        return result;
    }
}