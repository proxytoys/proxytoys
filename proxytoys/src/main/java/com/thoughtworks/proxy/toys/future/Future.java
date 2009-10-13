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
import java.util.concurrent.ExecutorService;
import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class Future {
    public static Object object(Object target) {
        return object(target, new StandardProxyFactory());
    }

    public static Object object(Object target, ProxyFactory proxyFactory) {
        Class[] types;
        Class targetClass = target.getClass();
        if (proxyFactory.canProxy(targetClass)) {
            types = new Class[]{targetClass};
        } else {
            Set<Class> classes = getAllInterfaces(targetClass);
            types = new Class[classes.size()];
            classes.toArray(types);
        }
        return object(types, target, proxyFactory, newCachedThreadPool());
    }

    public static Object object(Class[] types, Object target, ProxyFactory proxyFactory, ExecutorService executor) {
        FutureInvoker invoker = new FutureInvoker(target, proxyFactory, executor);
        return proxyFactory.createProxy(invoker, types);
    }
}