/*
 * Created 19.03.2010 by Joerg Schaible.
 * 
 * (c) 2010 ThoughtWorks
 *
 * See license.txt for license details
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

public class PrivilegingInvoker<T> extends DelegatingInvoker<T>
{
    private static final long serialVersionUID = 5352672950789740381L;
    private final ActionExecutor executor;

    public PrivilegingInvoker(ProxyFactory proxyFactory, ObjectReference<T> delegateReference, ActionExecutor executor)
    {
        super(proxyFactory, delegateReference, DelegationMode.DIRECT);
        this.executor = executor == null ? new AccessControllerExecutor() : executor;
    }

    public PrivilegingInvoker(T delegate, ActionExecutor executor)
    {
        this(new StandardProxyFactory(), new SimpleReference<T>(delegate), executor);
    }

    public PrivilegingInvoker(T delegate)
    {
        this(delegate, null);
    }

    @Override
    protected Object invokeOnDelegate(final Method method, final Object[] args) throws InvocationTargetException
    {
        try {
            return executor.execute(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception
                {
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
