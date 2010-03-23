/*
 * Created on 27-Jul-2004
 * 
 * (c) 2003-2005 ThoughtWorks
 *
 * See license.txt for license details
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
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author J&ouml;rg Schaible
 */
public class EchoDecorator extends Decorator {
    private static final long serialVersionUID = 1L;
    private final PrintWriter out;
    private final ProxyFactory factory;

    /**
     * Construct an EchoingDecorator.
     *
     * @param out     the {@link PrintWriter} receiving the logs
     * @param factory the {@link ProxyFactory} to use
     */
    public EchoDecorator(final PrintWriter out, final ProxyFactory factory) {
        this.out = out;
        this.factory = factory;
    }

    @Override
    public Object[] beforeMethodStarts(final Object proxy, final Method method, final Object[] args) {
        printMethodCall(method, args);
        return super.beforeMethodStarts(proxy, method, args);
    }

    @Override
    public Object decorateResult(final Object proxy, final Method method, final Object[] args, Object result) {
        final Class<?> returnType = method.getReturnType();
        printMethodResult(result);
        if (returnType != Object.class && factory.canProxy(returnType)) {
            result = Decorating.proxy(returnType).with(result, this).build(factory);
        } else if (result != null && returnType == Object.class && factory.canProxy(result.getClass())) {
            result = Decorating.proxy(result.getClass()).with(result, this).build(factory);
        }
        return result;
    }

    @Override
    public Throwable decorateTargetException(
            final Object proxy, final Method method, final Object[] args, final Throwable cause) {
        printTargetException(cause);
        return super.decorateTargetException(proxy, method, args, cause);
    }

    @Override
    public Exception decorateInvocationException(
            final Object proxy, final Method method, final Object[] args, final Exception cause) {
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
