package com.thoughtworks.proxytoys;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
abstract class AbstractProxyFactory implements ProxyFactory, Serializable {

    public static final Method getInvoker;

    static {
        try {
            getInvoker = InvokerReference.class.getMethod("getInvoker", null);
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    class AbstractInvocationHandlerAdapter implements Serializable {
        private final Invoker invoker;

        public AbstractInvocationHandlerAdapter(Invoker invocationInterceptor) {
            this.invoker = invocationInterceptor;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(AbstractProxyFactory.getInvoker)) {
                return invoker;
            }
            try {
                return invoker.invoke(proxy, method, args);
            } catch (UndeclaredThrowableException e) {
                throw e.getUndeclaredThrowable();
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    public Invoker getInvoker(Object proxy) {
        InvokerReference ih = (InvokerReference) proxy;
        return ih.getInvoker();
    }
}