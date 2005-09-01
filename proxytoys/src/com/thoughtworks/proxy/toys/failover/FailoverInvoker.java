/*
 * Created on 14-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.failover;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.SimpleReference;
import com.thoughtworks.proxy.toys.delegate.Delegating;
import com.thoughtworks.proxy.toys.hotswap.HotSwappingInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * {@link com.thoughtworks.proxy.Invoker Invoker} that implements a failover strategy by using different delegates in case of an
 * exception. The implemented strategy is a simple round-robin algorithm to change the delegate in case of a relevant exception.
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @since 0.1
 */
public class FailoverInvoker extends HotSwappingInvoker {
    private static final long serialVersionUID = -8289095570093619184L;
    private Object[] delegates;
    private Class exceptionClass;
    private int current;

    /**
     * Construct a FailoverInvoker.
     * 
     * @param types the types of the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     * @param delegates the delegates to use
     * @param exceptionClass the type of the exception
     * @throws IllegalArgumentException if <tt>exceptionClass</tt> is not a {@link Throwable}
     * @since 0.1
     */
    public FailoverInvoker(
            final Class[] types, final ProxyFactory proxyFactory, final Object[] delegates, final Class exceptionClass) {
        super(types, proxyFactory, new SimpleReference(delegates[0]), Delegating.STATIC_TYPING);
        if (!Throwable.class.isAssignableFrom(exceptionClass)) {
            throw new IllegalArgumentException("exceptionClass is not a Throwable");
        }
        this.delegates = delegates;
        this.exceptionClass = exceptionClass;
    }

    protected Object invokeOnDelegate(final Method method, final Object[] args) throws InvocationTargetException {
        Object result = null;
        final int original = current;
        while (result == null) {
            try {
                result = super.invokeOnDelegate(method, args);
                break;
            } catch (InvocationTargetException e) {
                if (exceptionClass.isInstance(e.getTargetException())) {
                    synchronized (this) {
                        current++;
                        current = current % delegates.length;
                        if (original == current) {
                            throw e;
                        }
                        hotswap(delegates[current]);
                    }
                } else {
                    throw e;
                }
            }
        }
        return result;
    }

}
