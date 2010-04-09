/*
 * (c) 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 19-03-2010.
 */
package com.thoughtworks.proxy.toys.privilege;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.SimpleReference;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;
import com.thoughtworks.proxy.toys.delegate.DelegationMode;


/**
 * {@link com.thoughtworks.proxy.Invoker Invoker} that creates for the invoked method a
 * {@link PrivilegedExceptionAction} and runs this action with the provided
 * {@link ActionExecutor}.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public class PrivilegingInvoker<T> extends DelegatingInvoker<T> {
    private static final long serialVersionUID = 5352672950789740381L;
    private final ActionExecutor executor;

    /**
     * Construct the invoker.
     * 
     * @param proxyFactory the proxy factory used to create the proxy
     * @param delegateReference the reference object managing the delegate for the call
     * @param executor the executor of the {@link PrivilegedExceptionAction}
     * @since 1.0
     */
    public PrivilegingInvoker(
        ProxyFactory proxyFactory, ObjectReference<T> delegateReference, ActionExecutor executor) {
        super(proxyFactory, delegateReference, DelegationMode.DIRECT);
        this.executor = executor == null ? new AccessControllerExecutor() : executor;
    }

    /**
     * Construct the invoker.
     * 
     * @param delegate the delegate for the call
     * @param executor the executor of the {@link PrivilegedExceptionAction}
     * @since 1.0
     */
    public PrivilegingInvoker(T delegate, ActionExecutor executor) {
        this(new StandardProxyFactory(), new SimpleReference<T>(delegate), executor);
    }

    /**
     * Construct the invoker using a {@link AccessControllerExecutor}.
     * 
     * @param delegate the delegate for the call
     * @since 1.0
     */
    public PrivilegingInvoker(T delegate) {
        this(delegate, null);
    }

    @Override
    protected Object invokeOnDelegate(final Method method, final Object[] args)
        throws InvocationTargetException {
        try {
            return executor.execute(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    return PrivilegingInvoker.super.invokeOnDelegate(method, args);
                }
            });
        } catch (PrivilegedActionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof InvocationTargetException) {
                throw InvocationTargetException.class.cast(cause);
            } else {
                throw new InvocationTargetException(cause);
            }
        }
    }
}
