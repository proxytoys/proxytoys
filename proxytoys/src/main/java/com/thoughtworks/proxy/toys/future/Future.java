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
 * Factory for proxy instances that run any method call concurrently and return the method
 * result later. Any method call for the proxied object will be called asynchronously. However,
 * the call itself will return immediately with another proxy for the result object. This is a
 * {@linkplain com.thoughtworks.proxy.toys.hotswap.HotSwapping hot swappable proxy} that
 * contains a {@linkplain com.thoughtworks.proxy.toys.nullobject.Null null object} until the
 * asynchronously called method returns. Then the result proxy is hot swapped with the real
 * result of the method.
 * 
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @since 1.0
 */
public class Future<T> {

    private Class<?>[] types;
    private Object target;

    private Future(Class<?>[] types) {
        this.types = types;
    }

    /**
     * Creates a proxy instance for asynchronous calls on a type. 
     * 
     * @param primaryType the type of the created proxy.
     * @return the proxy of the specified type.
     * @since 1.0
     */
    public static <T> FutureWith<T> proxy(Class<T> primaryType) {
        Future<T> future = new Future<T>(new Class<?>[]{primaryType});
        return new FutureWith<T>(future);
    }

    /**
     * Creates a proxy instance for asynchronous calls on a type. 
     * 
     * @param primaryType the main type of the created proxy.
     * @param types the other types of the created proxy.
     * @return the proxy of the specified types
     * @since 1.0
     */
    public static <T> FutureWith<T> proxy(Class<T> primaryType, Class<?>... types) {
        Class<?>[] allTypes = new Class[types.length+1];
        allTypes[0] = primaryType;
        System.arraycopy(types,0,allTypes,1,types.length);
        Future<T> future = new Future<T>(allTypes);
        return new FutureWith<T>(future);
    }

    /**
     * Creates a proxy instance for asynchronous calls on an object. 
     * 
     * @param target the proxied object.
     * @return the proxy.
     * @since 1.0
     */
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

        /**
         * Defines the object that shall be proxied. This object must implement the types used
         * to create the proxy.
         * 
         * @param target the object that shall be proxied.
         * @return the factory that will proxy instances of the supplied type.
         * @since upcoming
         */
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

        /**
         * Create a proxy with asynchronously called methods. The delegate must implement the
         * given types. The return values of the called methods must be non-final object types.
         * 
         * @param factory the {@link ProxyFactory} to use.
         * @return the created proxy implementing the <tt>types</tt> and {@link Swappable}
         * @since 1.0
         */
        public T build(ProxyFactory factory) {
            if (future.types == null) {
                Class<?> targetClass = future.target.getClass();
                if (factory.canProxy(targetClass)) {
                    future.types = new Class[]{targetClass};
                } else {
                    Set<Class<?>> classes = ReflectionUtils.getAllInterfaces(targetClass);
                    future.types = new Class[classes.size()];
                    classes.toArray(future.types);
                }
            }
            FutureInvoker invoker = new FutureInvoker(future.target, factory, Executors.newCachedThreadPool());
            return factory.<T>createProxy(invoker, future.types);
        }
    }
}