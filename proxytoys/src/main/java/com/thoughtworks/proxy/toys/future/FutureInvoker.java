/*
 *
 * (c) 2003-2009 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.future;

import static com.thoughtworks.proxy.toys.hotswap.HotSwapping.hotSwappable;
import static com.thoughtworks.proxy.toys.nullobject.Null.nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.Swappable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
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
            Object nullResult = nullable(returnType).build(proxyFactory);
            final Swappable swappableResult = Swappable.class.cast(hotSwappable(returnType).with(nullResult).build(proxyFactory));
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