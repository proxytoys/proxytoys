/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11-May-2004
 */
package com.thoughtworks.proxy.toys.hotswap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;
import com.thoughtworks.proxy.toys.delegate.DelegationMode;


/**
 * A {@link DelegatingInvoker} implementation that allows the exchange of the delegate.
 *
 * @author Aslak Helles&oslash;y
 * @author Dan North
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @since 0.1
 */
public class HotSwappingInvoker<T> extends DelegatingInvoker<Object> {
    private static final long serialVersionUID = 1L;
    private static final Method hotswap;
    private static final Method checkForCycle;

    static {
        try {
            hotswap = Swappable.class.getMethod("hotswap", new Class[]{Object.class});
            checkForCycle = CycleCheck.class.getMethod("checkForCycle");
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e.toString());
        }
    }

    /**
     * Internal interface used to detect cyclic swapping activity.
     *
     * @since 0.2
     */
    protected static interface CycleCheck {
        /**
         * Checks for a cyclic swap action.
         *
         * @throws IllegalStateException if cycle detected
         * @since 0.2
         */
        void checkForCycle();
    }

    private Class<?>[] types;
    private transient boolean executed = false;
    private transient ThreadLocal<Object> delegate;

    /**
     * Construct a HotSwappingInvoker.
     *
     * @param types             the types of the proxy
     * @param proxyFactory      the {@link ProxyFactory} to use
     * @param delegateReference the {@link ObjectReference} with the delegate
     * @param delegationMode    {@link DelegationMode#DIRECT} or {@link DelegationMode#SIGNATURE}
     * @since 1.0
     */
    public HotSwappingInvoker(
            final Class<?>[] types, final ProxyFactory proxyFactory, final ObjectReference<Object> delegateReference,
            final DelegationMode delegationMode) {
        super(proxyFactory, delegateReference, delegationMode);
        this.types = types;
        this.delegate = new ThreadLocal<Object>();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        try {
            delegate.set(delegate()); // ensure delegate will not change during invocation
            if (method.equals(hotswap)) {
                result = hotswap(args[0]);
            } else if (method.equals(checkForCycle)) {
                if (executed) {
                    throw new IllegalStateException("Cyclic dependency");
                } else {
                    if (delegate() instanceof CycleCheck) {
                        executed = true;
                        CycleCheck.class.cast(delegate()).checkForCycle();
                        executed = false;
                    }
                }
                return Void.TYPE;
            } else {
                result = super.invoke(proxy, method, args);
            }
        } finally {
            delegate.set(null);
        }
        return result;
    }

    @Override
    protected Object delegate() {
        final Object currentDelegate;
        currentDelegate = delegate.get();
        if (currentDelegate == null) {
            return super.delegate();
        } else {
            return currentDelegate;
        }
    }

    /**
     * Exchange the current delegate.
     *
     * @param newDelegate the new delegate
     * @return the old delegate
     * @throws IllegalStateException if cyclic swapping action is detected
     * @since 0.1
     */
    protected Object hotswap(final Object newDelegate) {
        ObjectReference<Object> ref = getDelegateReference();
        Object result = ref.get();
        // Note, for the cycle detection the delegate has to be set first
        delegate.set(newDelegate);
        ref.set(newDelegate);
        if (newDelegate instanceof CycleCheck) {
            CycleCheck.class.cast(newDelegate).checkForCycle();
        }
        return result;
    }

    /**
     * Create a proxy for this Invoker. The proxy implements all the types given as parameter to the constructor and
     * implements additionally the {@link Swappable} interface.
     *
     * @return the new proxy
     * @since 0.1
     */
    public T proxy() {
        Class<?>[] typesWithSwappable = new Class[types.length + 2];
        System.arraycopy(types, 0, typesWithSwappable, 0, types.length);
        typesWithSwappable[types.length] = Swappable.class;
        typesWithSwappable[types.length + 1] = CycleCheck.class;
        return getProxyFactory().<T>createProxy(this, typesWithSwappable);
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.delegate = new ThreadLocal<Object>();
    }
}
