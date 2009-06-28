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
import com.thoughtworks.proxy.toys.decorate.Decorating;

import java.io.PrintWriter;


/**
 * Factory for echoing proxy instances.
 * <p>
 * The Echoing toy acts as a decorator where every method invocation is written to a PrintWriter first.
 * </p>
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author J&ouml;rg Schaible
 * @since 0.1
 * @see com.thoughtworks.proxy.toys.echo
 */
public class Echoing {

    /**
     * Create a echoing proxy for a type and use system error channel to report.
     * 
     * @param type the proxied type
     * @return the generated proxy
     * @since 0.1
     */
    public static Object object(final Class type) {
        return object(type, new PrintWriter(System.err));
    }

    /**
     * Create a echoing proxy for a type and report to a given {@link PrintWriter}.
     * 
     * @param type the proxied type
     * @param out the PrintWriter receiving the output
     * @return the generated proxy
     * @since 0.1
     */
    public static Object object(final Class type, final PrintWriter out) {
        return object(type, null, out);
    }

    /**
     * Create a echoing proxy for a type that delegates to an object and use system error channel to report.
     * 
     * @param type the proxied type
     * @param impl the delegated object
     * @return the generated proxy
     * @since 0.1
     */
    public static Object object(final Class type, final Object impl) {
        return object(type, impl, new PrintWriter(System.err));
    }

    /**
     * Create a echoing proxy for a type that delegates to an object and use a special {@link ProxyFactory}
     * implementation as well as the system error channel to report.
     * 
     * @param type the proxied type
     * @param impl the delegated object
     * @param factory the ProxyFactory to use
     * @return the generated proxy
     * @since 0.2
     */
    public static Object object(final Class type, final Object impl, final ProxyFactory factory) {
        return object(type, impl, new PrintWriter(System.err), factory);
    }

    /**
     * Create a echoing proxy for a type that delegates to an object and report to a given {@link PrintWriter}.
     * 
     * @param type the proxied type
     * @param impl the delegated object
     * @param out the PrintWriter receiving the output
     * @return the generated proxy
     * @since 0.1
     */
    public static Object object(final Class type, final Object impl, final PrintWriter out) {
        return object(type, impl, out, new StandardProxyFactory());
    }

    /**
     * Create a echoing proxy for a type that delegates to an object and use a special {@link ProxyFactory}
     * implementation as well as reports to a given {@link PrintWriter}.
     * 
     * @param type the proxied type
     * @param impl the delegated object
     * @param out the PrintWriter receiving the output
     * @param factory the ProxyFactory to use
     * @return the generated proxy
     * @since 0.1
     */
    public static Object object(final Class type, final Object impl, final PrintWriter out, final ProxyFactory factory) {
        return Decorating.object(new Class[]{type}, impl, new EchoDecorator(out, factory), factory);
    }

    /** It's a factory, stupid */
    private Echoing() {
    }
}
