/*
 * Created on 27-Jul-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.echo;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.toys.decorate.Decorating;
import com.thoughtworks.proxy.toys.decorate.InvocationDecoratorSupport;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class EchoDecorator extends InvocationDecoratorSupport {
    private final PrintWriter out;
    private Class returnType;
    
    public EchoDecorator(PrintWriter out) {
        this.out = out;
    }
    
    public Object[] beforeMethodStarts(Object target, Method method, Object[] args) {
        returnType = method.getReturnType();
        printMethodCall(method, args);
        return super.beforeMethodStarts(target, method, args);
    }
    
    public Object decorateResult(Object result) {
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
