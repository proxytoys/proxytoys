package com.thoughtworks.proxy.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.thoughtworks.proxy.Invoker;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.2 $
 */
public class StandardProxyFactory extends AbstractProxyFactory {
    class StandardInvocationHandlerAdapter extends CoincidentalInvocationHandlerAdapter implements InvocationHandler {
        public StandardInvocationHandlerAdapter(Invoker invoker) {
            super(invoker);
        }
    }

    public Object createProxy(Class[] types, final Invoker invoker) {
        Class[] interfaces = new Class[types.length + 1];
        System.arraycopy(types, 0, interfaces, 0, types.length);
        interfaces[types.length] = InvokerReference.class;
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new StandardInvocationHandlerAdapter(invoker));
    }

    public boolean canProxy(Class type) {
        return type.isInterface();
    }

    public boolean isProxyClass(Class type) {
        return Proxy.isProxyClass(type);
    }

}