package com.thoughtworks.proxytoys;

import java.lang.reflect.Method;
import java.io.Serializable;

public interface Invoker extends Serializable {
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}