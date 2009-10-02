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
    private Class<T> type;
    private Decorator decorator;

    private Decorating(final Class<T> type) {
        this.type = type;
    }

    /**
     * Creates a factory for proxy instances that allow decoration.
     *
     * @param type     the type of the proxy when it is finally created.
     * @return a factory that will proxy instances of the supplied type.
     */
    public static <T> Decorating<T> decoratable(final Class<T> type) {
        return new Decorating<T>(type);
    }

    /**
     * specify the delegate object and the decorator
     *
     * @param delegate  the delegate
     * @param decorator the decorator
     * @return the factory that will proxy instances of the supplied type.
     */

    public Decorating<T> with(Object delegate, Decorator decorator) {
        this.delegate = delegate;
        this.decorator = decorator;
        return this;
    }

    /**
     * Creating a decorating proxy for an object using a special {@link StandardProxyFactory}
     *
     * @return the created proxy implementing the <tt>type</tt>
     */
    public T build() {
        return build(new StandardProxyFactory());
    }

    /**
     * Creating a decorating proxy for an object using a special {@link ProxyFactory}
     *
     * @param proxyFactory the @{link ProxyFactory} to use.
     * @return the created proxy implementing the <tt>type</tt>
     */

    public T build(final ProxyFactory proxyFactory) {
        return (T) proxyFactory.createProxy(new DecoratingInvoker(delegate, decorator), type);
    }
}
