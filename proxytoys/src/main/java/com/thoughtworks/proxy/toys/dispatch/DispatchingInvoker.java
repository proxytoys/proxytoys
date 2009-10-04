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
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.ReflectionUtils;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;
import static com.thoughtworks.proxy.toys.delegate.DelegationMode.DIRECT;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Invoker that dispatches all invocations to different objects according the membership of the method.
 *
 * @author J&ouml;rg Schaible after idea by Rickard &Ouml;berg
 */
public class DispatchingInvoker implements Invoker {
    private static final long serialVersionUID = 1L;
    private List types;
    private Invoker[] invokers;
    private transient Set<Method>[] methodSets;

    /**
     * Construct a DispatchingInvoker.
     *
     * @param proxyFactory       the {@link ProxyFactory} to use
     * @param types              the types of the generated proxy
     * @param delegateReferences the {@link ObjectReference ObjectReferences} for the delegates

     */
    public DispatchingInvoker(
            final ProxyFactory proxyFactory, final Class[] types, final ObjectReference[] delegateReferences) {
        this.types = Arrays.asList(types);
        invokers = new Invoker[types.length];
        methodSets = (Set<Method>[]) new Set[types.length];
        for (int i = 0; i < types.length; i++) {
            for (ObjectReference delegateReference : delegateReferences) {
                if (types[i].isAssignableFrom(delegateReference.get().getClass())) {
                    invokers[i] = new DelegatingInvoker(proxyFactory, delegateReference, DIRECT);
                    methodSets[i] = new HashSet<Method>(Arrays.asList(types[i].getMethods()));
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
     */
    protected DispatchingInvoker() {
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (method.equals(ReflectionUtils.equals)) {
            final Object arg = args[0];
            if (new StandardProxyFactory().isProxyClass(arg.getClass())
                    && ((InvokerReference) arg).getInvoker() instanceof DispatchingInvoker) {
                final DispatchingInvoker invoker = (DispatchingInvoker) ((InvokerReference) arg).getInvoker();
                if (new HashSet(types).equals(new HashSet(invoker.types))) {
                    boolean isEqual = true;
                    for (int i = 0; isEqual && i < types.size(); ++i) {
                        final Class type = (Class) types.get(i);
                        for (int j = 0; isEqual && j < invoker.types.size(); ++j) {
                            if (invoker.types.get(j).equals(type)) {
                                if (!invokers[i].equals(invoker.invokers[j])) {
                                    isEqual = false;
                                }
                            }
                        }
                        return isEqual;
                    }
                }
            }
            return Boolean.FALSE;
        } else if (method.equals(ReflectionUtils.hashCode)) {
            return hashCode();
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

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        final List<Class>[] types = (List<Class>[]) new List[methodSets.length];
        final List<String>[] names = (List<String>[]) new List[methodSets.length];
        final List<Class<?>[]>[] arguments = (List<Class<?>[]>[]) new List[methodSets.length];
        for (int i = 0; i < methodSets.length; i++) {
            final Method[] methods = methodSets[i].toArray(new Method[methodSets[i].size()]);
            types[i] = new ArrayList<Class>();
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
        final List[] types = (List[]) in.readObject();
        final List[] names = (List[]) in.readObject();
        final List[] arguments = (List[]) in.readObject();
        methodSets = (Set<Method>[]) new Set[types.length];
        try {
            for (int i = 0; i < methodSets.length; i++) {
                methodSets[i] = new HashSet<Method>();
                for (int j = 0; j < types[i].size(); j++) {
                    final Class type = (Class) types[i].get(j);
                    final String name = (String) names[i].get(j);
                    final Class[] argumentTypes = (Class[]) arguments[i].get(j);
                    methodSets[i].add(type.getMethod(name, argumentTypes));
                }
            }
        } catch (final NoSuchMethodException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }
}
