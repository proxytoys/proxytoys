/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxytoys;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class EchoProxy {

    private static class EchoInvocationHandler extends DelegatingProxy.DelegatingInvocationHandler {
        private final PrintWriter out;
        
        public EchoInvocationHandler(Object impl, PrintWriter out) {
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
                result = EchoProxy.newProxyInstance(method.getReturnType(), result, out);
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

    public static Object newProxyInstance(Class type) {
        return newProxyInstance(type, null, null);
    }
    
    public static Object newProxyInstance(Class type, PrintWriter out) {
        return newProxyInstance(type, null, out);
    }

    public static Object newProxyInstance(Class type, Object impl) {
        return newProxyInstance(type, impl, null);
    }

    public static Object newProxyInstance(Class type, Object impl, PrintWriter out) {
        return Proxy.newProxyInstance(type.getClassLoader(),
                new Class[] {type},
                new EchoInvocationHandler(impl, out));
    }
}
