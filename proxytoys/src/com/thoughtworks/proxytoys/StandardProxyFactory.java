package com.thoughtworks.proxytoys;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.2 $
 */
public class StandardProxyFactory extends AbstractProxyFactory {
    class StandardInvocationHandlerAdapter implements InvocationHandler, Serializable {
        private final Invoker invoker;

        public StandardInvocationHandlerAdapter(Invoker invocationInterceptor) {
            this.invoker = invocationInterceptor;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(AbstractProxyFactory.getInvoker)) {
                return invoker;
            }
            return invoker.invoke(proxy, method, args);
        }
    }

    public Object createProxy(Class type, final Invoker invoker) {
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{type, InvokerReference.class}, new StandardInvocationHandlerAdapter(invoker));
    }

    public boolean canProxy(Class type) {
        return type.isInterface();
    }

    public boolean isProxyClass(Class type) {
        return Proxy.isProxyClass(type);
    }

}