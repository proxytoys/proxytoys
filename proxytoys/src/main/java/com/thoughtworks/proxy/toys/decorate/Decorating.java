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
import com.thoughtworks.proxy.kit.ReflectionUtils;


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
public class Decorating<U, T> {

    private U delegate;
    private Class<?>[] types;
    private Decorator<T> decorator;
    
    private Decorating(final U delegate, final Class<T>primaryType, final Class<?>... types) {
    	this.delegate = delegate;
        this.types = ReflectionUtils.makeTypesArray(primaryType, types);
    }

    /**
     * Creates a factory for proxy instances that allow decoration.
     *
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <T> DecoratingWith<T> proxy(final Class<T> type) {
        return new DecoratingWith<T>(new Decorating<T, T>((T)null, type));
    }

    /**
     * Creates a factory for proxy instances that allow decoration.
     *
     * @param primaryType the primary type implemented by the proxy
     * @param types other types that are implemented by the proxy
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <T> DecoratingWith<T> proxy(final Class<T> primaryType, final Class<?> ... types) {
        return new DecoratingWith<T>(new Decorating<T, T>((T)null, primaryType, types));
    }

    /**
     * Creates a factory for proxy instances that allow decoration.
     * 
     * @param delegate  the delegate
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
	public static <U> DecoratingVisitor<U, U> proxy(final U delegate) {
	    @SuppressWarnings("unchecked")
        final Class<U> type = (Class<U>)delegate.getClass();
		return new DecoratingVisitor<U, U>(new Decorating<U, U>(delegate, type));
    }

    /**
     * Creates a factory for proxy instances that allow decoration.
     * 
     * @param delegate  the delegate
     * @param type the type of the proxy when it is finally created.
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <U, T> DecoratingVisitor<U, T> proxy(final U delegate, final Class<T> type) {
        return new DecoratingVisitor<U, T>(new Decorating<U, T>(delegate, type));
    }

    /**
     * Creates a factory for proxy instances that allow decoration.
     *
     * @param delegate  the delegate
     * @param primaryType the primary type implemented by the proxy
     * @param types other types that are implemented by the proxy
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <U, T> DecoratingVisitor<U, T> proxy(final U delegate, final Class<T> primaryType, final Class<?> ... types) {
        return new DecoratingVisitor<U, T>(new Decorating<U, T>(delegate, primaryType, types));
    }
    
    public static class DecoratingWith<T> {
        private Decorating<T, T> decorating;

        private DecoratingWith(Decorating<T, T> decorating) {
            this.decorating = decorating;
        }

        /**
         * specify the delegate
         *
         * @param delegate  the delegate
         * @return the factory that will proxy instances of the supplied type.
         * @since 1.0
         */
        public DecoratingVisitor<T, T> with(T delegate) {
            decorating.delegate = delegate;
            return new DecoratingVisitor<T, T>(decorating);
        }
    }

    public static class DecoratingVisitor<U, T> {
        private Decorating<U, T> decorating;

        private DecoratingVisitor(Decorating<U, T> decorating) {
            this.decorating = decorating;
        }

        /**
         * specify the visited decorator
         *
         * @param decorator the decorator
         * @return the factory that will proxy instances of the supplied type.
         * @since 1.0
         */
        public DecoratingBuild<U, T> visiting(Decorator<T> decorator) {
            decorating.decorator = decorator;
            return new DecoratingBuild<U, T>(decorating);
        }
    }

    public static class DecoratingBuild<U, T> {
        private Decorating<U, T> decorating;

        private DecoratingBuild(Decorating<U, T> decorating) {
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
            return proxyFactory.<T>createProxy(invoker, decorating.types);
        }
    }
}
