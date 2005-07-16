/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.factory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;

/**
 * An abstract implementation of a ProxyFactory.
 * <p>Precondition for the derived implementation is the
 * support of an interface for the invocation handler, that has the same methods with the same
 * signature as {@link java.lang.reflect.InvocationHandler}. Additionally it supports the method
 * {@link #getInvoker} of the proxy instance.</p>
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
abstract class AbstractProxyFactory implements ProxyFactory, Serializable {

    /** The getInvoker method. */
    public static final Method getInvoker;

    static {
        try {
            getInvoker = InvokerReference.class.getMethod("getInvoker", null);
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    /**
     * Generic implementation of a invocation handler with a JDK compatible method and signature.
     * <p>This is a serendipitous class - it can be extended, and the subclass
     * made to implement either <tt>{@link java.lang.reflect.InvocationHandler}</tt>
     * or the CGLIB <tt>{@link net.sf.cglib.proxy.InvocationHandler}</tt> because
     * they both conveniently have exactly the same <tt>invoke</tt> method with the
     * same signature.</p>
     *
     * <p>Clever, eh?</p>
     */
    class CoincidentalInvocationHandlerAdapter implements Serializable {
        private final Invoker invoker;

        /**
         * Construct a CoincidentalInvocationHandlerAdapter.
         * @param invocationInterceptor the invocation handler.
         */
        public CoincidentalInvocationHandlerAdapter(final Invoker invocationInterceptor) {
            this.invoker = invocationInterceptor;
        }

        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (method.equals(AbstractProxyFactory.getInvoker)) {
                return invoker;
            }
            try {
                return invoker.invoke(proxy, method, args);
            } catch (UndeclaredThrowableException e) {
                throw e.getUndeclaredThrowable();
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    /**
     * {@inheritDoc} The implementation of this method relies on the implementation of the derived
     * factory to add the interface {@link InvokerReference} to every proxy instance.
     */
    public Invoker getInvoker(final Object proxy) {
        final InvokerReference ih = (InvokerReference) proxy;
        return ih.getInvoker();
    }
}