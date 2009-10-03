/*
 * Created on 24-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
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
import static com.thoughtworks.proxy.toys.delegate.DelegationMode.DIRECT;
import static com.thoughtworks.proxy.toys.delegate.DelegationMode.SIGNATURE;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Invoker that delegates method calls to an object.
 * <p>
 * This forms the basis of many other proxy toys. The delegation behaviour was factored out of
 * <tt>HotSwappingInvoker</tt>.
 * </p>
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Dan North
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.proxy.toys.hotswap.HotSwappingInvoker
 * @since 0.1
 */
public class DelegatingInvoker implements Invoker {
    private static final long serialVersionUID = 1L;
    private transient Map<Method, Method> methodCache;
    private ProxyFactory proxyFactory;
    private ObjectReference delegateReference;
    private DelegationMode delegationMode;

    /**
     * Construct a DelegatingInvoker.
     *
     * @param proxyFactory      the {@link ProxyFactory} to use
     * @param delegateReference the {@link ObjectReference} of the delegate
     * @param delegationMode    one of the delegation modes
     * @throws IllegalArgumentException if the <tt>delegationMode</tt> is not one of the predefined constants of
     *                                  {@link Delegating}
     * @since 0.2
     */
    public DelegatingInvoker(final ProxyFactory proxyFactory, final ObjectReference delegateReference,
                             final DelegationMode delegationMode) {
        this.proxyFactory = proxyFactory;
        this.delegateReference = delegateReference;
        this.delegationMode = delegationMode;
        this.methodCache = new HashMap<Method, Method>();
    }

    /**
     * Construct a DelegatingInvoker with a {@link StandardProxyFactory} and {@link DelegationMode#SIGNATURE}.
     *
     * @param delegate the delegated object
     * @since 0.1
     */
    public DelegatingInvoker(final Object delegate) {
        this(new StandardProxyFactory(), new SimpleReference(delegate), SIGNATURE);
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
                    delegate = ((DelegatingInvoker) invoker).delegate();
                }
            }
            if (arg == null) {
                result = delegate == null ? Boolean.TRUE : Boolean.FALSE;
            } else {
                result = arg.equals(delegate) ? Boolean.TRUE : Boolean.FALSE;
            }
        } else if (method.equals(ReflectionUtils.hashCode)) {
            // equals and hashCode must be consistent
            result = delegate == null ? hashCode() : delegate.hashCode();

            // null delegatable
        } else if (delegate == null) {
            result = null;

            // regular method call
        } else {
            Method methodToCall = methodCache.get(method);
            if (methodToCall == null) {
                methodToCall = getMethodToInvoke(method, args);
                methodCache.put(method, methodToCall);
            }
            result = invokeOnDelegate(methodToCall, args);
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

    /**
     * Lookup a matching method. The lookup will only be done once for every method called on the proxy.
     *
     * @param method the invoked method on the proxy
     * @param args   the arguments for the invocation
     * @return the matching method
     * @throws DelegationException if no matching method can be found
     * @since 0.2
     */
    protected Method getMethodToInvoke(final Method method, final Object[] args) {
        if (delegationMode == DIRECT) {
            return method;
        } else {
            final String methodName = method.getName();
            try {
                return delegate().getClass().getMethod(methodName, method.getParameterTypes());
            } catch (Exception e) {
                throw new DelegationException("Unable to find method " + methodName, e, delegate());
            }
        }
    }

    /**
     * Invoke the given method on the delegate.
     *
     * @param method the method to invoke
     * @param args   the arguments for the invocation
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
     * Compares a DelegatingInvoker with another one for equality. Two DelegatingInvoker are equal, if they have both
     * the same <tt>delegation mode</tt> and their delegees are equal.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     * @since 0.2
     */
    public boolean equals(final Object obj) {
        if (obj instanceof DelegatingInvoker) {
            final DelegatingInvoker invoker = (DelegatingInvoker) obj;
            return invoker.delegationMode == delegationMode && delegate().equals(invoker.delegate());
        }
        return false;
    }

    public int hashCode() {
        final Object delegate = delegate();
        return delegationMode.delegationHashcode(delegate == null
                ? System.identityHashCode(this) : delegate.hashCode());
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        methodCache = new HashMap();
    }
}
