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
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.ReflectionUtils;
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
 * @since 0.1
 */
public class DelegatingInvoker implements Invoker {
    private static final long serialVersionUID = -4437574780659460771L;
    private ProxyFactory proxyFactory;
    private ObjectReference delegateReference;
    private boolean staticTyping;

    /**
     * Construct a DelegatingInvoker.
     * 
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param delegateReference the {@link ObjectReference} of the delegate
     * @param staticTyping {@link Delegating#STATIC_TYPING} or {@link Delegating#DYNAMIC_TYPING}
     * @since 0.1
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
     * @since 0.1
     */
    public DelegatingInvoker(final Object delegate) {
        this(new StandardProxyFactory(), new SimpleReference(delegate), Delegating.DYNAMIC_TYPING);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Object result;
        Object delegate = delegate();

        // equals(...) and hashCode()
        if (method.equals(ReflectionUtils.equals)) {
            // Note: equals will normally compare the classes directly, so we have to dereference
            // all delegates and then swap the call (in case of our argument is also a delegation proxy).
            final Object arg = args[0];
            while (delegate != null && proxyFactory.isProxyClass(delegate.getClass())) {
                Invoker invoker = proxyFactory.getInvoker(delegate);
                if (invoker instanceof DelegatingInvoker) {
                    delegate = ((DelegatingInvoker)invoker).delegate();
                }
            }
            if (arg == null) {
                result = delegate == null ? Boolean.TRUE : Boolean.FALSE;
            } else {
                result = arg.equals(delegate) ? Boolean.TRUE : Boolean.FALSE;
            }
        } else if (method.equals(ReflectionUtils.hashCode)) {
            // equals and hashCode must be consistent
            result = new Integer(delegate == null ? hashCode() : delegate.hashCode());

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
     * Retrieve the delegated object in derived classes.
     * 
     * @return the delegated object
     * @since 0.1
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
     * @since 0.1
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
     * @since 0.1
     */
    protected Method getDelegateMethod(final String methodName, final Class[] parameterTypes) {
        try {
            return delegate().getClass().getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new DelegationException("Unable to find method " + methodName, e, delegate());
        }
    }

    /**
     * Retrieve the {@link ObjectReference} of the delegate.
     * 
     * @return the reference of hte delegate
     * @since 0.2
     */
    protected ObjectReference getDelegateReference() {
        return this.delegateReference;
    }

    /**
     * Retrieve the {@link ProxyFactory} to use.
     * 
     * @return the ProxyFactory
     * @since 0.2
     */
    protected ProxyFactory getProxyFactory() {
        return this.proxyFactory;
    }

    /**
     * Compares a DelegatingInvoker with another one for equality. Two DelegatingInvoker are equal, if they have both the same
     * <tt>staticTyping</tt> flag and their delegees are equal.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     * @since 0.2
     */
    public boolean equals(final Object obj) {
        if (obj instanceof DelegatingInvoker) {
            final DelegatingInvoker invoker = (DelegatingInvoker)obj;
            return invoker.staticTyping == staticTyping && delegate().equals(invoker.delegate());
        }
        return false;
    }

    public int hashCode() {
        final Object delegate = delegate();
        int hashCode = delegate == null ? System.identityHashCode(this) : delegate.hashCode();
        if (staticTyping) {
            hashCode = ~hashCode;
        } else {
            hashCode = -hashCode;
        }
        return hashCode;
    }

}
