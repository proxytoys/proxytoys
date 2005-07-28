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
import com.thoughtworks.proxy.factory.InvokerReference;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.toys.delegate.Delegating;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Invoker that dispatches all invocations to different objects according the membership of the method.
 * 
 * @author J&ouml;rg Schaible after idea by Rickard &Ouml;berg
 * @since 0.2
 */
public class DispatchingInvoker implements Invoker {

    private final Invoker[] invokers;
    private final Set[] methodSets;
    private final List types;

    /**
     * Construct a DispatchinInvoker.
     * 
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param types the types of the generated proxy
     * @param delegateReferences the {@link ObjectReference ObjectReferences} for the delegates
     */
    public DispatchingInvoker(final ProxyFactory proxyFactory, final Class[] types, final ObjectReference[] delegateReferences) {
        this.types = Arrays.asList(types);
        invokers = new Invoker[types.length];
        methodSets = new Set[types.length];
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < delegateReferences.length; j++) {
                if (types[i].isAssignableFrom(delegateReferences[j].get().getClass())) {
                    invokers[i] = new DelegatingInvoker(proxyFactory, delegateReferences[j], Delegating.STATIC_TYPING);
                    methodSets[i] = new HashSet(Arrays.asList(types[i].getMethods()));
                    break;
                }
            }
            if (invokers[i] == null) {
                throw new DispatchingException("Cannot dispatch type " + types[i].getName(), types[i]);
            }
        }
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (method.equals(ReflectionUtils.equals)) {
            final Object arg = args[0];
            if (new StandardProxyFactory().isProxyClass(arg.getClass())
                    && ((InvokerReference)arg).getInvoker() instanceof DispatchingInvoker) {
                final DispatchingInvoker invoker = (DispatchingInvoker)((InvokerReference)arg).getInvoker();
                if (new HashSet(types).equals(new HashSet(invoker.types))) {
                    boolean isEqual = true;
                    for (int i = 0; isEqual && i < types.size(); ++i) {
                        final Class type = (Class)types.get(i);
                        for (int j = 0; isEqual && j < invoker.types.size(); ++j) {
                            if (invoker.types.get(j).equals(type)) {
                                if (!invokers[i].equals(invoker.invokers[j])) {
                                    isEqual = false;
                                }
                            }
                        }
                        return new Boolean(isEqual);
                    }
                }
            }
            return Boolean.FALSE;
        } else if (method.equals(ReflectionUtils.hashCode)) {
            return new Integer(hashCode());
        } else if (method.equals(ReflectionUtils.toString)) {
            return types.toString();
        } else {
            for (int i = 0; i < invokers.length; i++) {
                if (methodSets[i].contains(method)) {
                    return invokers[i].invoke(proxy, method, args);
                }
            }
        }
        throw new RuntimeException("Cannot dispatch method " + method.getName());
    }
}
