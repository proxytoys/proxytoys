/*
 * Created on 21-Mar-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxytoys;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * Utility class for creating Null Objects
 * 
 * A null object has deterministically boring behaviour as follows:
 * <ul>
 * <li>If a method returns a <em>primitive</em> the null object returns
 * the default for that type (e.g. <tt>false</tt> for <tt>boolean</tt>).
 * 
 * <li>If a method returns an array, the null object returns an empty
 * array, which is usually nicer than just returninng <tt>null</tt>.
 * (In fact an empty array is just the null object for arrays.)
 * 
 * <li>If the method returns an interface the null object returns
 * a new null object implementing that interface (so you can recurse
 * or step through object graphs without surprises).
 * 
 * <li>Otherwise the null object just returns <tt>null</tt>.
 * </ul>
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class Null {

    private static final class NullInvocationHandler implements InvocationHandler {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		    if (method.getReturnType().isInterface()) {
		        return object(method.getReturnType());
		    }
            else if (method.getReturnType().isArray()) {
                return Array.newInstance(method.getReturnType().getComponentType(), 0);
            }
		    else if (boolean.class == method.getReturnType()) {
		        return Boolean.FALSE;
		    }
		    else if (byte.class == method.getReturnType()) {
		        return new Byte((byte) 0);
		    }
		    else if (char.class == method.getReturnType()) {
		        return new Character((char) 0);
		    }
		    else if (int.class == method.getReturnType()) {
		        return new Integer(0);
		    }
		    else if (long.class == method.getReturnType()) {
		        return new Long(0);
		    }
		    else if (float.class == method.getReturnType()) {
		        return new Float(0.0);
		    }
		    else if (double.class == method.getReturnType()) {
		        return new Double(0.0);
		    }
		    else {
		        return null;
		    }
		}
	}

    /**
     * Create a new Null Object:
     * <pre>
     *     Foo foo = (Foo)Null.object(Foo.class); // null object
     * 
     *     Null.isNullObject(foo); // true
     * </pre>
     * 
     */
	public static Object object(Class type) {
		return Proxy.newProxyInstance(type.getClassLoader(),
                new Class[] {type},
                new NullInvocationHandler());
    }
    
    /**
     * Determine whether an object was created by {@link Null#object(Class)}
     */
    public static boolean isNullObject(Object object) {
        return Proxy.isProxyClass(object.getClass())
            && Proxy.getInvocationHandler(object) instanceof NullInvocationHandler;
    }
}
