/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.echo;

import java.io.PrintWriter;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;


/**
 * @deprecated EchoingInvoker is going to go away or be replaced with a
 *             <tt>DecoratingInvoker</tt> - DN
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
        return object(type, impl, null);
    }

    public static Object object(Class type, Object impl, PrintWriter out) {
        return object(type, impl, out, new StandardProxyFactory());
    }

    public static Object object(Class type, Object impl, PrintWriter out, ProxyFactory factory) {
        return factory.createProxy(new Class[] {type}, new EchoInvoker(impl, out));
    }
    
    /** It's a factory, stupid */
    private Echoing(){}
}
