/*
 * Created on 27-Jul-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.echo;

import com.thoughtworks.proxy.toys.decorate.Decorating;
import com.thoughtworks.proxy.toys.decorate.InvocationDecoratorSupport;

import java.io.PrintWriter;
import java.lang.reflect.Method;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class EchoDecorator extends InvocationDecoratorSupport {
    private final PrintWriter out;

    public EchoDecorator(PrintWriter out) {
        this.out = out;
    }

    public Object[] beforeMethodStarts(Object proxy, Method method, Object[] args) {
        printMethodCall(method, args);
        return super.beforeMethodStarts(proxy, method, args);
    }

    public Object decorateResult(Object proxy, Method method, Object[] args, Object result) {
        Class returnType = method.getReturnType();
        if (returnType.isInterface()) {
            result = Decorating.object(returnType, result, this);
        }
        return result;
    }

    private void printMethodCall(Method method, Object[] args) {
        StringBuffer buf = new StringBuffer(method.getDeclaringClass().getName());
        buf.append(".").append(method.getName());

        if (args == null) {
            args = new Object[0];
            buf.append("(");
        }
        for (int i = 0; i < args.length; i++) {
            buf.append(i == 0 ? "(" : ", ").append(args[i]);
        }
        buf.append(")");
        out.println(buf);
        out.flush();
    }
}
