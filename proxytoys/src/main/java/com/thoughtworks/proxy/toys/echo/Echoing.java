/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04-Feb-2004
 */
package com.thoughtworks.proxy.toys.echo;

import java.io.PrintWriter;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.decorate.Decorating;

/**
 * Factory for echoing proxy instances.
 * <p>
 * The Echoing toy acts as a decorator where every method invocation is written to a PrintWriter first.
 * </p>
 *
 * @author Dan North
 * @author J&ouml;rg Schaible
 * @author Juan Li
 * @author Paul Hammant
 * @see com.thoughtworks.proxy.toys.echo
 * @since 0.1
 */
public class Echoing<T> {

    private Class<T> type;
    private Object delegate;
    private PrintWriter printWriter = new PrintWriter(System.err);

    private Echoing(final Class<T> type) {
        this.type = type;
    }

    /**
     * Creates a factory for proxy instances that allow delegation.
     *
     * @param type the type of the proxy when it is finally created.
     * @return a factory that will proxy instances of the supplied type.
     * @since 1.0
     */
    public static <T> EchoingWithOrTo<T> proxy(final Class<T> type) {
        return new EchoingWithOrTo<T>(new Echoing<T>(type));
    }

    public static class EchoingWithOrTo<T> extends EchoingTo<T> {

        private EchoingWithOrTo(Echoing<T> echoing) {
            super(echoing);
        }

        /**
         * Specify the delegate.
         *
         * @param delegate the object the proxy delegates to.
         * @return the factory that will proxy instances of the supplied type.
         * @since 1.0
         */
        public EchoingTo<T> with(final Object delegate) {
            echoing.delegate = delegate;
            return new EchoingTo<T>(echoing);
        }

    }

    public static class EchoingTo<T> {
        protected Echoing<T> echoing;

        private EchoingTo(Echoing<T> echoing) {
            this.echoing = echoing;
        }

        /**
         * Specify the printWriter.
         *
         * @param printWriter which receives the output
         * @return the factory that will proxy instances of the supplied type.
         * @since 1.0
         */
        public EchoingBuild<T> to(final PrintWriter printWriter) {
            echoing.printWriter = printWriter;
            return new EchoingBuild<T>(echoing);
        }

    }

    public static class EchoingBuild<T> {
        private Echoing<T> echoing;

        private EchoingBuild(Echoing<T> echoing) {
            this.echoing = echoing;
        }


        /**
         * Creating a delegating proxy for an object using the {@link StandardProxyFactory}.
         *
         * @return the created proxy implementing the <tt>type</tt>
         * @since 1.0
         */
        public T build() {
            return build(new StandardProxyFactory());
        }

        /**
         * Creating a delegating proxy for an object using a special {@link ProxyFactory}.
         *
         * @param proxyFactory the {@link ProxyFactory} to use.
         * @return the created proxy implementing the <tt>type</tt>
         * @since 1.0
         */
        public T build(final ProxyFactory proxyFactory) {
            EchoDecorator decorator = new EchoDecorator(echoing.printWriter, proxyFactory);
            return Decorating.proxy(echoing.type).with(echoing.delegate, decorator).build(proxyFactory);
        }
    }
}
