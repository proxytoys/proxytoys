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

    public Echoing withPrintWriter(final PrintWriter printWriter) {
        this.printWriter = printWriter;
        return this;
    }

    public Echoing withDelegateObject(final Object delegate) {
        this.delegate = delegate;
        return this;
    }

    public Object build() {
        return build(new StandardProxyFactory());
    }

    public Object build(final ProxyFactory proxyFactory) {
        return decoratable(new Class[]{type}).with(delegate, new EchoDecorator(printWriter, proxyFactory)).build(proxyFactory);
    }

    public static <T> Echoing<T> echo(final Class<T> type) {
        return new Echoing<T>(type);
    }

    private Echoing(final Class<T> type) {
        this.type = type;
    }
}
