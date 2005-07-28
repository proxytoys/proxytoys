/*
 * Created on 24-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.delegate;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.SimpleReference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Invoker that delegates method calls to an object.
 * <p>
 * This forms the basis of many other proxy toys. The delegation behaviour was factored out of <tt>HotSwappingInvoker</tt>.
 * </p>
 * 
 * @see com.thoughtworks.proxy.toys.hotswap.HotSwappingInvoker
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Dan North
 * @author J&ouml;rg Schaible
 */
public class DelegatingInvoker implements Invoker {

    /** the {@link ProxyFactory} in use */
    protected final ProxyFactory proxyFactory;
    /** the {@link ObjectReference} of the delegate */
    protected final ObjectReference delegateReference;
    private final boolean staticTyping;

    /**
     * Construct a DelegatingInvoker.
     * 
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param delegateReference the {@link ObjectReference} of the delegate
     * @param staticTyping {@link Delegating#STATIC_TYPING} or {@link Delegating#DYNAMIC_TYPING}
     */
    public DelegatingInvoker(final ProxyFactory proxyFactory, final ObjectReference delegateReference, final boolean staticTyping) {
        this.proxyFactory = proxyFactory;
        this.delegateReference = delegateReference;
        this.staticTyping = staticTyping;
    }

    /**
     * Construct a DelegatingInvoker with a {@link StandardProxyFactory} and {@link Delegating#DYNAMIC_TYPING}.
     * 
     * @param delegate the delegated object
     */
    public DelegatingInvoker(final Object delegate) {
        this(new StandardProxyFactory(), new SimpleReference(delegate), Delegating.DYNAMIC_TYPING);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Object result;
        final Object delegate = delegate();

        // equals(...) and hashCode()
        if (method.equals(ReflectionUtils.equals)) {
            // TODO this whole section is really ugly and needs cleaning up
            Object arg = args[0];
            while (arg != null && proxyFactory.isProxyClass(arg.getClass())) {
                Invoker invoker = proxyFactory.getInvoker(arg);
                if (invoker instanceof DelegatingInvoker) {
                    arg = ((DelegatingInvoker)invoker).delegate();
                }
            }
            if (delegate == null) {
                result = arg == null ? Boolean.TRUE : Boolean.FALSE;
            } else {
                result = delegate.equals(arg) ? Boolean.TRUE : Boolean.FALSE;
            }
        } else if (method.equals(ReflectionUtils.hashCode)) {
            result = new Integer(hashCode());

            // null delegate
        } else if (delegate == null) {
            result = null;

            // regular method call
        } else {
            result = invokeOnDelegate(getMethodToInvoke(method), args);
        }
        return result;
    }

    /**
     * @return the delegated object
     */
    protected Object delegate() {
        return delegateReference.get();
    }

    private Method getMethodToInvoke(final Method method) {
        if (staticTyping) {
            return method;
        } else {
            try {
                return getDelegateMethod(method.getName(), method.getParameterTypes());
            } catch (Exception e) {
                throw new DelegationException("Problem invoking " + method, e, delegate());
            }
        }
    }

    /**
     * Invoke the given method on the delegate.
     * 
     * @param method the method to invoke
     * @param args the arguments for the invocation
     * @return the method's result
     * @throws InvocationTargetException if the invoked method throws any exception
     */
    protected Object invokeOnDelegate(final Method method, final Object[] args) throws InvocationTargetException {
        final Object delegate = delegate();
        try {
            return method.invoke(delegate, args);
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new DelegationException("Problem invoking " + method, e, delegate);
        }
    }

    /**
     * Lookup a matching method on the delegate.
     * 
     * @param methodName the name of the searched method
     * @param parameterTypes the argument types of the method
     * @return the matching method
     * @throws DelegationException if no matching method can be found
     */
    protected Method getDelegateMethod(final String methodName, final Class[] parameterTypes) {
        try {
            return delegate().getClass().getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new DelegationException("Unable to find method " + methodName, e, delegate());
        }
    }

    /**
     * Compares a DelegatingInvoker with another one for equality. Two DelegatingInvoker are equal, if they have both the same
     * <tt>staticTyping</tt> flag and their delegees are equal.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj) {
        if (obj instanceof DelegatingInvoker) {
            final DelegatingInvoker invoker = (DelegatingInvoker)obj;
            return invoker.staticTyping == staticTyping && delegate().equals(invoker.delegate());
        }
        return false;
    }

}
