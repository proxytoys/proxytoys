package com.thoughtworks.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;

public interface Invoker extends Serializable {
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}