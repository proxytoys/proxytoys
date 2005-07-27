/*
 * Created on 24-Mar-2004
 *
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.nullobject;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Factory for creating Null Objects.
 * <p>
 * A null object instantiated by this toy has deterministically boring behaviour as follows:
 * <ul>
 * <li>If a method's return type is a <em>primitive</em>, the null object returns the default for that type (e.g.
 * <tt>false</tt> for <tt>boolean</tt>).
 * <li>If a method's return type is an array, the null object returns an empty array, which is usually nicer than just
 * returninng <tt>null</tt>. (In fact an empty array is just the null object for arrays.)
 * <li>If a method's return type is {@link Object}, an Object is returned.
 * <li>If the method's return type is any other type, the null object returns one of the following:
 * <ul>
 * <li>If the currently installed {@link ProxyFactory} can create a proxy for the type, a new null object for that type is
 * returned (so you can recurse or step through object graphs without surprises). (For the standard proxy factory this requires
 * the return type to be an interface).
 * <li>If the proxyFactory cannot create a proxy for the type, null is returned.
 * </ul>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author <a href="mailto:nospamx.aslak@thoughtworks.com">Aslak Helles&oslash;y</a>
 */
public class Null {

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

    public static Object object(Class type, ProxyFactory proxyFactory) {
        final Object result;

        // Primitives
        if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            result = Boolean.FALSE;
        } else if (byte.class.equals(type) || Byte.class.equals(type)) {
            result = new Byte((byte)0);
        } else if (char.class.equals(type) || Character.class.equals(type)) {
            result = new Character((char)0);
        } else if (int.class.equals(type) || Integer.class.equals(type)) {
            result = new Integer(0);
        } else if (long.class.equals(type) || Long.class.equals(type)) {
            result = new Long(0);
        } else if (float.class.equals(type) || Float.class.equals(type)) {
            result = new Float(0.0);
        } else if (double.class.equals(type) || Double.class.equals(type)) {
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
        } else if (Map.class == type) {
            result = Collections.EMPTY_MAP;
        } else if (List.class == type) {
            result = Collections.EMPTY_LIST;
        } else if (SortedSet.class == type) {
            result = NULL_SORTED_SET;
        } else if (SortedMap.class == type) {
            result = NULL_SORTED_MAP;
        } else if (proxyFactory.canProxy(type)) {
            result = proxyFactory.createProxy(new Class[]{type}, new NullInvoker(type, proxyFactory));
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Create a new Null Object:
     * 
     * <pre>
     *         Foo foo = (Foo)
     * <tt>
     * Null.object
     * </tt>
     *    (Foo.class);
     * </pre>
     */
    public static Object object(Class type) {
        return object(type, new StandardProxyFactory());
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
        return proxyFactory.isProxyClass(object.getClass()) && proxyFactory.getInvoker(object) instanceof NullInvoker;
    }

    /** It's a factory, stupid */
    private Null() {
    }
}