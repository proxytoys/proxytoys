package com.thoughtworks.proxy.factory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;

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

    /**
     * <p>This is a serendipitous class - it can be extended, and the subclass
     * made to implement either <tt>java.lang.reflect.InvocationHandler</tt> or
     * the CGLIB <tt>InvocationHandler</tt> because they both conveniently
     * have exactly the same <tt>invoke</tt> method with the same signature.</p>
     * 
     * <p>Clever, eh?</p>
     */
    class CoincidentalInvocationHandlerAdapter implements Serializable {
        private final Invoker invoker;

        public CoincidentalInvocationHandlerAdapter(Invoker invocationInterceptor) {
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