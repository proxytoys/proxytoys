/*
 * Created on 04-Feb-2004
 *
 * (c) 2003-2005 ThoughtWorks
 *
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.echo;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import static com.thoughtworks.proxy.toys.decorate.Decorating.decoratable;

import java.io.PrintWriter;


/**
 * Factory for echoing proxy instances.
 * <p>
 * The Echoing toy acts as a decorator where every method invocation is written to a PrintWriter first.
 * </p>
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.proxy.toys.echo
 * @since 0.1
 */
public class Echoing<T> {

    private Class<T> type;
    private Object delegate;
    private PrintWriter printWriter = new PrintWriter(System.err);

    /**
     * specify the printWriter
     *
     * @param printWriter which receives the output
     * @return the factory that will proxy instances of the supplied type.
     */
    public Echoing<T> to(final PrintWriter printWriter) {
        this.printWriter = printWriter;
        return this;
    }

    /**
     * specify the delegate
     *
     * @param delegate the object the proxy delegates to.
     * @return the factory that will proxy instances of the supplied type.
     */
    public Echoing<T> with(final Object delegate) {
        this.delegate = delegate;
        return this;
    }

    /**
     * Creating a delegating proxy for an object using a special {@link StandardProxyFactory}
     *
     * @return the created proxy implementing the <tt>type</tt>
     */
    public T build() {
        return build(new StandardProxyFactory());
    }

    /**
     * Creating a delegating proxy for an object using a special {@link ProxyFactory}
     *
     * @param proxyFactory the @{link ProxyFactory} to use.
     * @return the created proxy implementing the <tt>type</tt>
     */
    public T build(final ProxyFactory proxyFactory) {
        return (T) decoratable(type).with(delegate, new EchoDecorator(printWriter, proxyFactory)).build(proxyFactory);
    }

    /**
     * Creates a factory for proxy instances that allow delegation.
     *
     * @param type the type of the proxy when it is finally created.
     * @return a factory that will proxy instances of the supplied type.
     */

    public static <T> Echoing<T> echoable(final Class<T> type) {
        return new Echoing<T>(type);
    }

    private Echoing(final Class<T> type) {
        this.type = type;
    }
}
