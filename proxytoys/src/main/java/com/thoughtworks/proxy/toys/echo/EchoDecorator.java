/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 27-Jul-2004
 */
package com.thoughtworks.proxy.toys.echo;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.decorate.Decorating;
import com.thoughtworks.proxy.toys.decorate.Decorator;

/**
 * A {@link com.thoughtworks.proxy.toys.decorate.Decorator} implementation that echoes any invocation to a {@link PrintWriter}.
 * <p>
 * The implementation will try to create new proxies for every return value, that can be proxied by the
 * {@link ProxyFactory} in use.
 * </p>
 *
 * @author Dan North
 * @author J&ouml;rg Schaible
 * @since 0.1
 */
public class EchoDecorator<T> extends Decorator<T> {
    private static final long serialVersionUID = 1L;
    private final PrintWriter out;
    private final ProxyFactory factory;

    /**
     * Construct an EchoingDecorator.
     *
     * @param out     the {@link PrintWriter} receiving the logs
     * @param factory the {@link ProxyFactory} to use
     * @since 0.2
     */
    public EchoDecorator(final PrintWriter out, final ProxyFactory factory) {
        this.out = out;
        this.factory = factory;
    }

    @Override
    public Object[] beforeMethodStarts(final T proxy, final Method method, final Object[] args) {
        printMethodCall(method, args);
        return super.beforeMethodStarts(proxy, method, args);
    }

	@Override
    @SuppressWarnings("unchecked")
    public Object decorateResult(final T proxy, final Method method, final Object[] args, Object result) {
        Class returnType = method.getReturnType();
        printMethodResult(result);
        if (returnType != Object.class && factory.canProxy(returnType)) {
            result = Decorating.proxy(result, returnType).visiting(this).build(factory);
        } else if (result != null && returnType == Object.class && factory.canProxy(result.getClass())) {
            returnType = result.getClass();
			result = Decorating.proxy(result, returnType).visiting(this).build(factory);
        }
        return result;
    }

    @Override
    public Throwable decorateTargetException(
            final T proxy, final Method method, final Object[] args, final Throwable cause) {
        printTargetException(cause);
        return super.decorateTargetException(proxy, method, args, cause);
    }

    @Override
    public Exception decorateInvocationException(
            final T proxy, final Method method, final Object[] args, final Exception cause) {
        printInvocationException(cause);
        return super.decorateInvocationException(proxy, method, args, cause);
    }

    private void printMethodCall(Method method, Object[] args) {
        final StringBuilder buf = new StringBuilder("[");
        buf.append(Thread.currentThread().getName());
        buf.append("] ");
        buf.append(method.getDeclaringClass().getName());
        buf.append(".").append(method.getName());

        if (args == null) {
            args = new Object[0];
        }
        buf.append("(");
        for (int i = 0; i < args.length; i++) {
            buf.append(i == 0 ? "<" : ", <").append(args[i]).append(">");
        }
        buf.append(") ");
        out.print(buf);
        out.flush();
    }

    private void printMethodResult(final Object result) {
        final StringBuilder buf = new StringBuilder("--> <");
        buf.append(result == null ? "NULL" : result.toString());
        buf.append(">");
        out.println(buf);
        out.flush();
    }

    private void printTargetException(final Throwable throwable) {
        final StringBuilder buf = new StringBuilder("throws ");
        buf.append(throwable.getClass().getName());
        buf.append(": ");
        buf.append(throwable.getMessage());
        out.println(buf);
        out.flush();
    }

    private void printInvocationException(final Throwable throwable) {
        out.print("INTERNAL ERROR, ");
        printTargetException(throwable);
    }
}
