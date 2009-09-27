/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.nullobject;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;
import static com.thoughtworks.proxy.toys.nullobject.Null.nullable;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;


/**
 * A {@link Invoker} implementation that returns always new Null objects.
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @since 0.1
 */
public class NullInvoker implements Invoker {
    private static final long serialVersionUID = -4713875509846468548L;
    private static final Method toString;

    static {
        try {
            toString = Object.class.getMethod("toString", new Class[0]);
        } catch (Exception e) {
            throw new InternalError("toString() missing!");
        }
    }

    private Class type;
    private ProxyFactory proxyFactory;

    /**
     * Construct a NullInvoker.
     *
     * @param type         the type of the proxy
     * @param proxyFactory the {@link ProxyFactory} to use
     */
    public NullInvoker(final Class type, final ProxyFactory proxyFactory) {
        this.type = type;
        this.proxyFactory = proxyFactory;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Object result;

        // Object methods
        if (toString.equals(method)) {
            result = "Null Object for " + type.getName();
        } else if (ReflectionUtils.equals.equals(method)) {
            Object other = args[0];
            result = (Null.isNullObject(other, proxyFactory) && type.equals(getType(other)))
                    ? Boolean.TRUE
                    : Boolean.FALSE;
        } else if (ReflectionUtils.hashCode.equals(method)) {
            result = new Integer(type.hashCode());
        }

        // Just another null object
        else {
            result = nullable(method.getReturnType()).build(proxyFactory);
        }
        return result;
    }

    private Class getType(Object object) {
        final Class result;
        if (proxyFactory.isProxyClass(object.getClass())) {
            NullInvoker nullInvoker = (NullInvoker) proxyFactory.getInvoker(object);
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
