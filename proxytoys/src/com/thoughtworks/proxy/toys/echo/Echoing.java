/*
 * Created on 04-Feb-2004
 *
 * (c) 2003-2004 ThoughtWorks
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
 */
public class Echoing {

    public static Object object(Class type) {
        return object(type, null, null);
    }

    public static Object object(Class type, PrintWriter out) {
        return object(type, null, out);
    }

    public static Object object(Class type, Object impl) {
        return object(type, impl, new PrintWriter(System.err));
    }

    public static Object object(Class type, Object impl, PrintWriter out) {
        return object(type, impl, out, new StandardProxyFactory());
    }

    public static Object object(Class type, Object impl, PrintWriter out, ProxyFactory factory) {
        return Decorating.object(type, impl, new EchoDecorator(out));
    }

    /** It's a factory, stupid */
    private Echoing() {
    }
}
