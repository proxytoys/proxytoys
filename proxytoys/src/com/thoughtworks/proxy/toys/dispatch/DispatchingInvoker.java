/*
 * Created on 24-Feb-2005
 * 
 * (c) 2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.dispatch;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;
import com.thoughtworks.proxy.toys.delegate.ObjectReference;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Invoker that dispatches all invocations to different objects according the membership of the
 * method.
 * 
 * @author J&ouml;rg Schaible after idea by Rickard &Ouml;berg
 * @since 0.2
 */
public class DispatchingInvoker implements Invoker {

    private final Invoker[] invoker;
    private final Set[] methodSets;

    public DispatchingInvoker(ProxyFactory proxyFactory, Class[] types, ObjectReference[] delegateReferences) {
        invoker = new Invoker[types.length];
        methodSets = new Set[types.length];
        for (int i = 0; i < types.length; i++) {
            invoker[i] = new DelegatingInvoker(proxyFactory, delegateReferences[i], DelegatingInvoker.EXACT_METHOD);
            methodSets[i] = new HashSet(Arrays.asList(types[i].getMethods()));
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for (int i = 0; i < invoker.length; i++) {
            if (methodSets[i].contains(method)) {
                return invoker[i].invoke(proxy, method, args);
            }
        }
        throw new RuntimeException("Cannot dispatch method " + method.getName());
    }

}
