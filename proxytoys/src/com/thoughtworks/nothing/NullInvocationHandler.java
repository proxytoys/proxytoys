/*
 * Created on 24-Mar-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.nothing;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Invocation handler for Null Objects
 * 
 * @see Null
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author <a href="mailto:aslak@thoughtworks.com">Aslak Helles&oslash;y</a>
 */

class NullInvocationHandler implements InvocationHandler, Serializable {
    private static final Method equals;
    private static final Method hashCode;
    private static final Method toString;
    
    static {
        try {
            equals = Object.class.getMethod("equals", new Class[]{Object.class});
            hashCode = Object.class.getMethod("hashCode", new Class[0]);
            toString = Object.class.getMethod("toString", new Class[0]);
        } catch (Exception e) {
            throw new InternalError("hashCode(), equals(Object) or toString() missing!");
        }
    }

    private Class type;
    
    public NullInvocationHandler(Class type) {
        this.type = type;
    }
    
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        
		// Object methods
		if (toString.equals(method)) {
            result = "Null Object for " + type.getName();
        }
        else if (equals.equals(method)) {
            Object other = args[0];
            result = (Null.isNullObject(other)
                    && type.equals(getType(other)))
                ? Boolean.TRUE : Boolean.FALSE;
        }
        else if (hashCode.equals(method)) {
			result = new Integer(type.hashCode());
        }
        
        // Null interface methods
        else {
            result = Null.object(method.getReturnType());
        }
        return result;
	}

	private Class getType(Object object) {
        final Class result;
        if (Proxy.isProxyClass(object.getClass())) {
            NullInvocationHandler handler = (NullInvocationHandler) Proxy.getInvocationHandler(object);
            result = handler.type;
        }
        else {
            result = object.getClass();
        }
        return result;
	}
    
    // Serialization
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (!nullObjectIsSerializable()) {
		    throw new NotSerializableException(type.getName());
		}
        out.writeObject(type);
    }

	private boolean nullObjectIsSerializable() {
		return Serializable.class.isAssignableFrom(type);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        type = (Class) in.readObject();
    }
}