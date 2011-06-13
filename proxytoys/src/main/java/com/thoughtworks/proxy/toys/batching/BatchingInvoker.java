package com.thoughtworks.proxy.toys.batching;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

public class BatchingInvoker implements Invoker {
    private final Set<Invocation> invocations = new LinkedHashSet<Invocation>();
    Invoker delegate;
    ProxyFactory factory;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.equals(ReflectionUtils.equals) || method.equals(ReflectionUtils.hashCode)) {
            return delegate.invoke(proxy, method, args);
        } else {
            Invocation invocation = new Invocation(factory, proxy, method, args);
            invocations.remove(invocation); // Remove any equal invocations
            invocations.add(invocation);
            return invocation.result;
        }
    }

    public void flush() throws Throwable {
        for (Invocation invocation : invocations) {
            invocation.invoke(delegate);
        }
    }

}
