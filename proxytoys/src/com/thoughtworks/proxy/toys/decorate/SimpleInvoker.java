package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.Invoker;

import java.lang.reflect.Method;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
class SimpleInvoker implements Invoker {
    private final Object target;

    public SimpleInvoker(Object target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(target, args);
    }
}
