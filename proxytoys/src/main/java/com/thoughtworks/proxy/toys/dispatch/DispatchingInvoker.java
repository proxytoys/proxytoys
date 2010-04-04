/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24-Feb-2005
 */
package com.thoughtworks.proxy.toys.dispatch;

import static com.thoughtworks.proxy.toys.delegate.DelegationMode.DIRECT;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.InvokerReference;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.ReflectionUtils;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;


/**
 * Invoker that dispatches all invocations to different objects according the membership of the method.
 *
 * @author J&ouml;rg Schaible after idea by Rickard &Ouml;berg
 * @since 0.2
 */
public class DispatchingInvoker implements Invoker {
    private static final long serialVersionUID = 1L;
    private List<Class<?>> types;
    private Invoker[] invokers;
    private transient Set<Method>[] methodSets;
    private transient Method[] toStringMethods;

    /**
     * Construct a DispatchingInvoker.
     *
     * @param proxyFactory       the {@link ProxyFactory} to use
     * @param types              the types of the generated proxy
     * @param delegateReferences the {@link ObjectReference ObjectReferences} for the delegates
     * @since 0.2
     */
    public DispatchingInvoker(
            final ProxyFactory proxyFactory, final Class<?>[] types, final ObjectReference<Object>[] delegateReferences) {
        this.types = Arrays.asList(types);
        invokers = new Invoker[types.length];
        toStringMethods = new Method[types.length];
        @SuppressWarnings("unchecked")
        Set<Method>[] sets = new Set[types.length];
        methodSets = sets;
        for (int i = 0; i < types.length; i++) {
            for (final ObjectReference<Object> delegateReference : delegateReferences) {
                if (types[i].isAssignableFrom(delegateReference.get().getClass())) {
                    invokers[i] = new DelegatingInvoker<Object>(proxyFactory, delegateReference, DIRECT);
                    methodSets[i] = new HashSet<Method>(Arrays.asList(types[i].getMethods()));
                    for (Method method : methodSets[i]) {
                        if (method.getName().equals("toString") && method.getParameterTypes().length == 0) {
                            toStringMethods[i] = method;
                            break;
                        }
                    }
                    break;
                }
            }
            if (invokers[i] == null) {
                throw new DispatchingException("Cannot dispatch type " + types[i].getName(), types[i]);
            }
        }
    }

    /**
     * Constructor used by pure reflection serialization.
     * 
     * @since 0.2
     */
    protected DispatchingInvoker() {
    }

    public Object invoke(final Object proxy, Method method, final Object[] args) throws Throwable {
        if (method.equals(ReflectionUtils.equals)) {
            final Object arg = args[0];
            if (new StandardProxyFactory().isProxyClass(arg.getClass())
                    && (InvokerReference.class.cast(arg)).getInvoker() instanceof DispatchingInvoker) {
                final DispatchingInvoker invoker = DispatchingInvoker.class.cast((InvokerReference.class.cast(arg)).getInvoker());
                if (types.size() == invoker.types.size()) {
                    boolean isEqual = true;
                    for (int i = 0; isEqual && i < types.size(); ++i) {
                        final Class<?> type = types.get(i);
                        for (int j = 0; isEqual && j < invoker.types.size(); ++j) {
                            if (invoker.types.get(j).equals(type)) {
                                if (!invokers[i].equals(invoker.invokers[j])) {
                                    isEqual = false;
                                }
                            }
                        }
                    }
                    return isEqual;
                }
            }
            return Boolean.FALSE;
        } else if (method.equals(ReflectionUtils.hashCode)) {
            return hashCode();
        } else if (method.equals(ReflectionUtils.toString)) {
            for (int i = 0; i < invokers.length; i++) {
                Method toString = toStringMethods[i];
                if (toString != null && toString.getDeclaringClass().isAssignableFrom(proxy.getClass())) {
                    return invokers[i].invoke(proxy, method, args);
                }
            }
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

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        @SuppressWarnings("unchecked")
        final List<Class<?>>[] types = new List[methodSets.length];
        @SuppressWarnings("unchecked")
        final List<String>[] names = new List[methodSets.length];
        @SuppressWarnings("unchecked")
        final List<Class<?>[]>[] arguments = new List[methodSets.length];
        for (int i = 0; i < methodSets.length; i++) {
            final Method[] methods = methodSets[i].toArray(new Method[methodSets[i].size()]);
            types[i] = new ArrayList<Class<?>>();
            names[i] = new ArrayList<String>();
            arguments[i] = new ArrayList<Class<?>[]>();
            for (Method method : methods) {
                types[i].add(method.getDeclaringClass());
                names[i].add(method.getName());
                arguments[i].add(method.getParameterTypes());
            }
        }
        out.writeObject(types);
        out.writeObject(names);
        out.writeObject(arguments);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        @SuppressWarnings("unchecked")
        final List<Class<?>>[] types = List[].class.cast(in.readObject());
        @SuppressWarnings("unchecked")
        final List<String>[] names = List[].class.cast(in.readObject());
        @SuppressWarnings("unchecked")
        final List<Class<?>[]>[] arguments = List[].class.cast(in.readObject());
        @SuppressWarnings("unchecked")
        final Set<Method>[] set = new Set[types.length];
        methodSets = set;
        toStringMethods = new Method[types.length];
        try {
            for (int i = 0; i < methodSets.length; i++) {
                methodSets[i] = new HashSet<Method>();
                for (int j = 0; j < types[i].size(); j++) {
                    final Class<?> type = types[i].get(j);
                    final String name = names[i].get(j);
                    final Class<?>[] argumentTypes = arguments[i].get(j);
                    final Method method = type.getMethod(name, argumentTypes);
                    methodSets[i].add(method);
                    if (name.equals("toString") && argumentTypes.length == 0) {
                        toStringMethods[i] = method;
                    }
                }
            }
        } catch (final NoSuchMethodException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }
}
