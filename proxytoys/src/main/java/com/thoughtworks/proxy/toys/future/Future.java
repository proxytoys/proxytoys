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

import java.util.Set;
import java.util.concurrent.Executors;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;

/**
 * @author Aslak Helles&oslash;y
 * @since 1.0
 */
// TODO: Javadoc
public class Future<T> {

    private Class<?>[] types;
    private Object target;

    private Future(Class<?>[] types) {
        this.types = types;
    }

    public static <T> FutureWith<T> proxy(Class<T> primaryType) {
        Future<T> future = new Future<T>(new Class<?>[]{primaryType});
        return new FutureWith<T>(future);
    }

    public static <T> FutureWith<T> proxy(Class<T> primaryType, Class<?>... types) {
        Class<?>[] allTypes = new Class[types.length+1];
        allTypes[0] = primaryType;
        System.arraycopy(types,0,allTypes,1,types.length);
        Future<T> future = new Future<T>(allTypes);
        return new FutureWith<T>(future);
    }

    public static <T> FutureBuild<T> proxy(T target) {
        Future<T> future = new Future<T>(null);
        future.target = target;
        return new FutureBuild<T>(future);
    }

    public static class FutureWith<T> {
        private Future<T> future;
        private FutureWith(Future<T> future) {
            this.future = future;
        }
        public FutureBuild<T> with(Object target) {
            future.target = target;
            return new FutureBuild<T>(future);
        }
    }

    public static class FutureBuild<T> {
        private Future<T> future;
        private FutureBuild(Future<T> future) {
            this.future = future;
        }

        public T build() {
            return build(new StandardProxyFactory());
        }

        public T build(ProxyFactory proxyFactory) {
            if (future.types == null) {
                Class<?> targetClass = future.target.getClass();
                if (proxyFactory.canProxy(targetClass)) {
                    future.types = new Class[]{targetClass};
                } else {
                    Set<Class<?>> classes = ReflectionUtils.getAllInterfaces(targetClass);
                    future.types = new Class[classes.size()];
                    classes.toArray(future.types);
                }
            }
            FutureInvoker invoker = new FutureInvoker(future.target, proxyFactory, Executors.newCachedThreadPool());
            return proxyFactory.<T>createProxy(invoker, future.types);
        }
    }
}