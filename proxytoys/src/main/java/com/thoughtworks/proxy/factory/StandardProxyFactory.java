/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.thoughtworks.proxy.Invoker;

/**
 * A {@link com.thoughtworks.proxy.ProxyFactory} based on a JDK.
 *
 * @author Aslak Helles&oslash;y
 * @since 0.1
 * @see com.thoughtworks.proxy.factory
 */
public class StandardProxyFactory extends AbstractProxyFactory {

    private static final long serialVersionUID = 4430360631813383235L;

    /**
     * The native InvocationHandler implementation.
     *
     * @since 0.1
     */
    static class StandardInvocationHandlerAdapter extends CoincidentalInvocationHandlerAdapter implements
            InvocationHandler {
        private static final long serialVersionUID = 141954540221604284L;

        /**
         * Construct a StandardInvocationHandlerAdapter.
         *
         * @param invoker the wrapping invoker instance
         * @since 0.1
         */
        public StandardInvocationHandlerAdapter(Invoker invoker) {
            super(invoker);
        }
    }

    public <T> T createProxy(final Invoker invoker, final Class<?>... types) {
        final Class<?>[] interfaces = new Class[types.length + 1];
        System.arraycopy(types, 0, interfaces, 0, types.length);
        interfaces[types.length] = InvokerReference.class;
        @SuppressWarnings("unchecked")
        final T proxyInstance = (T)Proxy.newProxyInstance(getClass().getClassLoader(), interfaces,
                new StandardInvocationHandlerAdapter(invoker));
        return proxyInstance;
    }

    public boolean canProxy(final Class<?> type) {
        return type.isInterface();
    }

    public boolean isProxyClass(final Class<?> type) {
        return Proxy.isProxyClass(type);
    }

}