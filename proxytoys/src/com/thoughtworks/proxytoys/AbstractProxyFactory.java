package com.thoughtworks.proxytoys;

import java.lang.reflect.Method;
import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public abstract class AbstractProxyFactory implements ProxyFactory, Serializable {

    public static final Method getInvocationInterceptor;

    static {
        try {
            getInvocationInterceptor = InvokerReference.class.getMethod("getInvoker", null);
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    public Invoker getInvoker(Object proxy) {
        InvokerReference ih = (InvokerReference) proxy;
        return ih.getInvoker();
    }
}