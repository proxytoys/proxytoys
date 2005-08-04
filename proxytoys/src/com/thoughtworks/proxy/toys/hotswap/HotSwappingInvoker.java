/*
 * Created on 11-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.hotswap;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;

import java.lang.reflect.Method;


/**
 * A {@link DelegatingInvoker} implementation that allows the exchange of the delegate.
 * 
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @since 0.1
 */
public class HotSwappingInvoker extends DelegatingInvoker {
    private static final Method hotswap;
    private static final Method checkForCycle;

    static {
        try {
            hotswap = Swappable.class.getMethod("hotswap", new Class[]{Object.class});
            checkForCycle = CycleCheck.class.getMethod("checkForCycle", null);
        } catch (NoSuchMethodException e) {
            throw new InternalError();
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

    private final Class[] types;
    private transient boolean executed = false;
    private final transient ThreadLocal delegate;

    /**
     * Construct a HotSwappingInvoker.
     * 
     * @param types the types of the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param delegateReference the {@link ObjectReference} with the delegate
     * @param staticTyping {@link com.thoughtworks.proxy.toys.delegate.Delegating#STATIC_TYPING STATIC_TYPING} or
     *            {@link com.thoughtworks.proxy.toys.delegate.Delegating#DYNAMIC_TYPING DYNAMIC_TYPING}
     * @since 0.1
     */
    public HotSwappingInvoker(
            final Class[] types, final ProxyFactory proxyFactory, final ObjectReference delegateReference,
            final boolean staticTyping) {
        super(proxyFactory, delegateReference, staticTyping);
        this.types = types;
        this.delegate = new ThreadLocal();
    }

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
                        ((CycleCheck)delegate()).checkForCycle();
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
        Object result = delegateReference.get();
        // Note, for the cycle detection the delegate have to be set first
        delegate.set(newDelegate);
        delegateReference.set(newDelegate);
        if (newDelegate instanceof CycleCheck) {
            ((CycleCheck)newDelegate).checkForCycle();
        }
        return result;
    }

    /**
     * Create a proxy for this Invoker. The proxy implements all the types given as parameter to the constructor and implements
     * additionally the {@link Swappable} interface.
     * 
     * @return the new proxy
     * @since 0.1
     */
    public Object proxy() {
        Class[] typesWithSwappable = new Class[types.length + 2];
        System.arraycopy(types, 0, typesWithSwappable, 0, types.length);
        typesWithSwappable[types.length] = Swappable.class;
        typesWithSwappable[types.length + 1] = CycleCheck.class;
        return proxyFactory.createProxy(typesWithSwappable, this);
    }
}
