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
 * {@link com.thoughtworks.proxy.Invoker Invoker} that implements transparent asynchronous
 * method calls. The invoked method will return immediately with a result that can be
 * {@linkplain HotSwapping hot swapped}. This result proxy contains first a {@linkplain Null
 * null object} and will automatically replaced later on when the asynchronous method call
 * returns the correct result.
 * 
 * @author Aslak Helles&oslash;y
 * @since 1.0
 */
public class FutureInvoker implements Invoker {
    private static final long serialVersionUID = 1L;
    private final Object target;
    private final ProxyFactory proxyFactory;
    private final ExecutorService executor;

    /**
     * Construct the invoker.
     * 
     * @param target the instance that will have its methods called asynchronously
     * @param proxyFactory the proxy factory used to create the proxy for the target instance
     *            and all return types of the called methods
     * @param executor the executor used to call the method asynchronously
     * @since 1.0
     */
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