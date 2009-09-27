/*
 * Created on 03-May-2004
 *
 * (c) 2003-2005 ThoughtWorks
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;


// TODO: use the AOP alliance API

/**
 * Toy factory to create proxies decorating an object in an AOP style.
 * <p>
 * An InvocationDecorator is used for the additional functionality. It is called before the original method is called,
 * after the original method was called, after the original method has thrown an exceptionor when an exception occurs,
 * calling the method of the decorated object.
 * </p>
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.proxy.toys.decorate
 * @since 0.1
 */
public class Decorating<T> {
    
    private Object delegate;
    private Class<T>[] types;
    private InvocationDecorator decorator;

    private Decorating(final Class<T>... types) {
        this.types = types;
    }

    public static <T> Decorating<T> decoratable(final Class<T> type) {
        return new Decorating<T>(type);
    }

    public static <T> Decorating<T> decoratable(final Class<T>[] types) {
        return new Decorating<T>(types);
    }

    public Decorating<T> with(Object delegate, InvocationDecorator decorator) {
        this.delegate = delegate;
        this.decorator = decorator;
        return this;
    }

    public T build() {
        return (T) new StandardProxyFactory().createProxy(types, new DecoratingInvoker(delegate, decorator));
    }

    public T build(final ProxyFactory proxyFactory) {
        return (T) proxyFactory.createProxy(types, new DecoratingInvoker(delegate, decorator));
    }
}
