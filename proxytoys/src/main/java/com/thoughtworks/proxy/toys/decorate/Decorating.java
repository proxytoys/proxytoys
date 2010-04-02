/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03-May-2004
 */
package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;


/**
 * Toy factory to create proxies decorating an object in an AOP style.
 * <p>
 * An InvocationDecorator is used for the additional functionality. It is called before the original method is called,
 * after the original method was called, after the original method has thrown an exception or when an exception occurs,
 * calling the method of the decorated object.
 * </p>
 *
 * @author Dan North
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Jian Li
 * @author Paul Hammant
 * @see com.thoughtworks.proxy.toys.decorate
 * @since 0.1
 */
public class Decorating<T> {

    private Object delegate;
    private Class<T> type;
    private Decorator decorator;

    private Decorating(final Class<T> type) {
        this.type = type;
    }

    /**
     * Creates a factory for proxy instances that allow decoration.
     * 
     * @param type the type of the proxy when it is finally created.
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <T> DecoratingWith<T> proxy(final Class<T> type) {
        return new DecoratingWith<T>(new Decorating<T>(type));
    }

    public static <T> DecoratingWith<T> proxy(final Class<T> primaryType, final Class<?> ... types) {
        // TODO: Provide this functionality again
        throw new UnsupportedOperationException("TODO");
    }

    public static class DecoratingWith<T> {
        private Decorating<T> decorating;

        private DecoratingWith(Decorating<T> decorating) {
            this.decorating = decorating;
        }

        /**
         * specify the delegate object and the decorator
         *
         * @param delegate  the delegate
         * @param decorator the decorator
         * @return the factory that will proxy instances of the supplied type.
         * @since 1.0
         */
        public DecoratingBuild<T> with(Object delegate, Decorator decorator) {
            decorating.delegate = delegate;
            decorating.decorator = decorator;
            return new DecoratingBuild<T>(decorating);
        }
    }

    public static class DecoratingBuild<T> {
        private Decorating<T> decorating;

        private DecoratingBuild(Decorating<T> decorating) {
            this.decorating = decorating;
        }

        /**
         * Creating a decorating proxy for an object using the {@link StandardProxyFactory}.
         *
         * @return the created proxy implementing the <tt>type</tt>
         * @since 1.0
         */
        public T build() {
            return build(new StandardProxyFactory());
        }

        /**
         * Creating a decorating proxy for an object using a special {@link ProxyFactory}.
         *
         * @param proxyFactory the {@link ProxyFactory} to use.
         * @return the created proxy implementing the <tt>type</tt>
         * @since 1.0
         */
        public T build(final ProxyFactory proxyFactory) {
            DecoratingInvoker<T> invoker = new DecoratingInvoker<T>(decorating.delegate, decorating.decorator);
            return proxyFactory.<T>createProxy(invoker, decorating.type);
        }
    }
}
