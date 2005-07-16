/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.nullobject;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ClassHierarchyIntrospector;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class NullInvoker implements Invoker {
    private static final Method toString;

    static {
        try {
            toString = Object.class.getMethod("toString", new Class[0]);
        } catch (Exception e) {
            throw new InternalError("toString() missing!");
        }
    }

    private final Class type;
    private final ProxyFactory proxyFactory;

    public NullInvoker(Class type, ProxyFactory proxyFactory) {
        this.type = type;
        this.proxyFactory = proxyFactory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;

        // Object methods
        if (toString.equals(method)) {
            result = "Null Object for " + type.getName();
        } else if (ClassHierarchyIntrospector.equals.equals(method)) {
            Object other = args[0];
            result = (Null.isNullObject(other, proxyFactory) && type.equals(getType(other))) ? Boolean.TRUE : Boolean.FALSE;
        } else if (ClassHierarchyIntrospector.hashCode.equals(method)) {
            result = new Integer(type.hashCode());
        }

        // Just another null object
        else {
            result = Null.object(method.getReturnType(), proxyFactory);
        }
        return result;
    }

    private Class getType(Object object) {
        final Class result;
        if (proxyFactory.isProxyClass(object.getClass())) {
            NullInvoker nullInvoker = (NullInvoker)proxyFactory.getInvoker(object);
            result = nullInvoker.type;
        } else {
            result = object.getClass();
        }
        return result;
    }

    // Serialization

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (!nullObjectIsSerializable()) {
            throw new NotSerializableException(type.getName());
        }
        out.defaultWriteObject();
    }

    private boolean nullObjectIsSerializable() {
        return Serializable.class.isAssignableFrom(type);
    }
}
