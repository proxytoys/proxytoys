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
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.thoughtworks.proxytoys.Invoker;
import com.thoughtworks.proxytoys.ProxyFactory;
import com.thoughtworks.proxytoys.StandardProxyFactory;

/**
 * Utility class for creating Null Objects.
 *
 * A null object has deterministically boring behaviour as follows:
 * <ul>
 * <li>If a method's return type is a <em>primitive</em>, the null object returns
 * the default for that type (e.g. <tt>false</tt> for <tt>boolean</tt>).
 *
 * <li>If a method's return type is an array, the null object returns an empty
 * array, which is usually nicer than just returninng <tt>null</tt>.
 * (In fact an empty array is just the null object for arrays.)
 *
 * <li>If a method's return type is {@link Object}, an Object is returned.

 * <li>If the method's return type is any other type, the null object returns
 * one of the following:
 * <ul>
 * <li>If the currently installed {@link ProxyFactory} can create a proxy for the type,
 * a new null object for that type is returned (so you can recurse
 * or step through object graphs without surprises). (For the standard proxy factory
 * this requires the return type to be an interface).
 * <li>If the proxyFactory cannot create a proxy for the type, null is returned.
 * </ul>
 * </ul>
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author <a href="mailto:nospamx.aslak@thoughtworks.com">Aslak Helles&oslash;y</a>
 */
public class Null implements Invoker {
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

    public static final Object NULL_OBJECT = new Object();

	/** Immutable Null Object implementation of {@link SortedMap} */
	public static final SortedMap NULL_SORTED_MAP = new TreeMap() {
		public Object put(Object key, Object value) {
			throw new UnsupportedOperationException();
		}
		public void clear() {
			throw new UnsupportedOperationException();
		}
		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}
		public Set keySet() {
			return Collections.EMPTY_SET;
		}
		public Collection values() {
			return Collections.EMPTY_LIST;
		}
		public Set entrySet() {
			return Collections.EMPTY_SET;
		}
	};

    /** Immutable Null Object implementation of {@link SortedSet} */
	public static final SortedSet NULL_SORTED_SET = new TreeSet() {
		public boolean add(Object o) {
			throw new UnsupportedOperationException();
		}
		public void clear() {
			throw new UnsupportedOperationException();
		}
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
		public boolean removeAll(Collection c) {
			throw new UnsupportedOperationException();
		}
		public boolean retainAll(Collection c) {
			throw new UnsupportedOperationException();
		}
	};

    private final Class type;
    private final ProxyFactory proxyFactory;

    public Null(Class type, ProxyFactory proxyFactory) {
        this.type = type;
        this.proxyFactory = proxyFactory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;

		// Object methods
		if (toString.equals(method)) {
            result = "Null Object for " + type.getName();
        }
        else if (equals.equals(method)) {
            Object other = args[0];
            result = (isNullObject(other, proxyFactory)
                    && type.equals(getType(other)))
                ? Boolean.TRUE : Boolean.FALSE;
        }
        else if (hashCode.equals(method)) {
			result = new Integer(type.hashCode());
        }

        // Just another null object
        else {
            result = object(method.getReturnType(), proxyFactory);
        }
        return result;
	}

    public static Object object(Class type, ProxyFactory proxyFactory) {
        final Object result;

        // Primitives
        if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            result = Boolean.FALSE;
        }
        else if (byte.class.equals(type) || Byte.class.equals(type)) {
            result = new Byte((byte) 0);
        }
        else if (char.class.equals(type) || Character.class.equals(type)) {
            result = new Character((char) 0);
        }
        else if (int.class.equals(type) || Integer.class.equals(type)) {
            result = new Integer(0);
        }
        else if (long.class.equals(type) || Long.class.equals(type)) {
            result = new Long(0);
        }
        else if (float.class.equals(type) || Float.class.equals(type)) {
            result = new Float(0.0);
        }
        else if (double.class.equals(type) || Double.class.equals(type)) {
            result = new Double(0.0);
        }

        // String
        else if (String.class.equals(type)) {
            result = "";
        }

        // Object
        else if (Object.class.equals(type)) {
            result = NULL_OBJECT;
        }

        // Arrays
        else if (type.isArray()) {
            result = Array.newInstance(type.getComponentType(), 0);
        }

        // Collections
        else if (Set.class == type) {
            result = Collections.EMPTY_SET;
        }
        else if (Map.class == type) {
            result = Collections.EMPTY_MAP;
        }
        else if (List.class == type) {
            result = Collections.EMPTY_LIST;
        }
        else if (SortedSet.class == type) {
            result = NULL_SORTED_SET;
        }
        else if (SortedMap.class == type) {
            result = NULL_SORTED_MAP;
        }
        else if (proxyFactory.canProxy(type)) {
            result = proxyFactory.createProxy(new Class[]{type}, new Null(type, proxyFactory));
        }
        else {
            result = null;
        }
        return result;
    }

    /**
     * Create a new Null Object:
     * <pre>
     *     Foo foo = (Foo)<tt>Null.object</tt>(Foo.class);
     * </pre>
     */
    public static Object object(Class type) {
        return object(type, new StandardProxyFactory());
    }

	private Class getType(Object object) {
        final Class result;
        if (proxyFactory.isProxyClass(object.getClass())) {
            Null nullInvoker = (Null) proxyFactory.getInvoker(object);
            result = nullInvoker.type;
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
        out.defaultWriteObject();
    }

	private boolean nullObjectIsSerializable() {
		return Serializable.class.isAssignableFrom(type);
	}

    /**
     * Determine whether an object was created by {@link Null#object(Class)}
     */
    public static boolean isNullObject(Object object) {
        return isNullObject(object, new StandardProxyFactory());
    }

    /**
     * Determine whether an object was created by {@link Null#object(Class, ProxyFactory)}
     */
    public static boolean isNullObject(Object object, ProxyFactory proxyFactory) {
        return isStandardNullObject(object) || isNullProxyObject(object, proxyFactory);
    }

	private static boolean isStandardNullObject(Object object) {
		return object == Collections.EMPTY_LIST
        || object == Collections.EMPTY_SET
        || object == Collections.EMPTY_MAP
        || object == NULL_SORTED_SET
        || object == NULL_SORTED_MAP
        || object == NULL_OBJECT;
	}

	private static boolean isNullProxyObject(Object object, ProxyFactory proxyFactory) {
		return proxyFactory.isProxyClass(object.getClass())
		&& proxyFactory.getInvoker(object) instanceof Null;
	}
}