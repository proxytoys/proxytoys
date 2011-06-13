package com.thoughtworks.proxy.toys.batching;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;
import com.thoughtworks.proxy.toys.hotswap.Swappable;
import com.thoughtworks.proxy.toys.nullobject.Null;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class Invocation {
    private final Object proxy;
    private final Method method;
    private final Object[] args;
    public Swappable result = null;

    public Invocation(ProxyFactory factory, Object proxy, Method method, Object[] args) {
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

    @Override
    public boolean equals(Object obj) {
        Invocation other = (Invocation) obj;
        return proxy.equals(other.proxy) && method.equals(other.method) && uniqueArgs().equals(other.uniqueArgs());
    }

    @Override
    public int hashCode() {
        int a = proxy.hashCode();

        int hash = proxy.hashCode();
        hash = 31 * hash + (method.hashCode());
        hash = 31 * hash + (uniqueArgs().hashCode());
        return hash;
    }

    private List<Object> uniqueArgs() {
        List<Object> uniqueArgs = new ArrayList<Object>();
        int argIndex = 0;
        for (Annotation[] parameterAnnotations : method.getParameterAnnotations()) {
            for (Annotation parameterAnnotation : parameterAnnotations) {
                if(parameterAnnotation.annotationType().equals(Unique.class)) {
                    uniqueArgs.add(args[argIndex]);
                    break;
                }
            }
            argIndex++;
        }
        if(uniqueArgs.isEmpty()) {
            uniqueArgs = asList(args);
        }
        return uniqueArgs;
    }
}
