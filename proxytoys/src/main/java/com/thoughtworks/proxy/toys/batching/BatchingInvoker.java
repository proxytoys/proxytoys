package com.thoughtworks.proxy.toys.batching;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;
import com.thoughtworks.proxy.toys.hotswap.Swappable;
import com.thoughtworks.proxy.toys.nullobject.Null;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BatchingInvoker implements Invoker {
    private final List<Invocation> invocations = new ArrayList<Invocation>();
    Invoker delegate;
    ProxyFactory factory;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = new Invocation(proxy, method, args);
        invocations.add(invocation);
        return invocation.result;
    }

    public void flush() throws Throwable {
        for (Invocation invocation : invocations) {
            invocation.invoke(delegate);
        }
    }

    private class Invocation {
        private final Object proxy;
        private final Method method;
        private final Object[] args;
        private Swappable result = null;

        public Invocation(Object proxy, Method method, Object[] args) {
            this.proxy = proxy;
            this.method = method;
            this.args = args;

            Class returnType = method.getReturnType();
            if (!returnType.equals(void.class)) {
                Object nullResult = Null.proxy(returnType).build(factory);
                Object swappableResult = HotSwapping.<Object>proxy(returnType).with(nullResult).build(factory);
                result = Swappable.class.cast(swappableResult);
            }

        }

        public void invoke(Invoker delegate) throws Throwable {
            Object newResult = delegate.invoke(proxy, method, args);
            result.hotswap(newResult);
        }
    }
}
