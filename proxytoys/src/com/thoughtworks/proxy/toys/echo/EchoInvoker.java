/*
 * Created on 10-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.echo;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;


class EchoInvoker extends DelegatingInvoker {
    private final PrintWriter out;
    
    public EchoInvoker(Object impl, PrintWriter out) {
        super(impl);
        if (out != null) {
            this.out = out;
        }
        else {
            // use stderr
            this.out = new PrintWriter(new OutputStreamWriter(System.err));
        }
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        printMethodCall(method, args);
        
        Object result = super.invoke(proxy, method, args);
        
        if (method.getReturnType().isInterface()) {
            result = Echoing.object(method.getReturnType(), result, out);
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