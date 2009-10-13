/*
 *
 * (c) 2003-2009 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.future;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import static com.thoughtworks.proxy.kit.ReflectionUtils.getAllInterfaces;

import java.util.Set;
import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class Future<T> {

    private Class[] types;
    private Object target;

    private Future(Class[] types) {
        this.types = types;
    }

    public static <T> FutureWith<T> typedFuture(Class<T> primaryType, Class... types) {
        Class[] allTypes = new Class[types.length+1];
        allTypes[0] = primaryType;
        System.arraycopy(types,0,allTypes,1,types.length);
        Future<T> future = new Future<T>(allTypes);

        return new FutureWith<T>(future);
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

    public static FutureBuild future(Object target) {
        Future future = new Future(null);
        future.target = target;
        return new FutureBuild(future);
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
                Class targetClass = future.target.getClass();
                if (proxyFactory.canProxy(targetClass)) {
                    future.types = new Class[]{targetClass};
                } else {
                    Set<Class> classes = getAllInterfaces(targetClass);
                    future.types = new Class[classes.size()];
                    classes.toArray(future.types);
                }
            }
            FutureInvoker invoker = new FutureInvoker(future.target, proxyFactory, newCachedThreadPool());
            return (T) proxyFactory.createProxy(invoker, future.types);
        }
    }
}