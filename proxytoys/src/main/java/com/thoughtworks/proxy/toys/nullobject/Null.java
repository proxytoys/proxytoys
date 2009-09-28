/*
 * Created on 24-Mar-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
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
 * Toy factory to create proxies acting as Null Objects.
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author <a href="mailto:nospamx.aslak@thoughtworks.com">Aslak Helles&oslash;y</a>
 * @since 0.1
 * @see com.thoughtworks.proxy.toys.nullobject
 */
public class Null<T> {

    /** The Null {@link Object}. */
    public static final Object NULL_OBJECT = new Object();

    /** Immutable Null Object implementation of {@link SortedMap} */
    public static final SortedMap NULL_SORTED_MAP = new TreeMap() {
        private static final long serialVersionUID = -4388170961744587609L;

        public Object put(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            // nothing to do
        }

        public Object remove(Object key) {
            return null;
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
        private static final long serialVersionUID = 809722154285517876L;

        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            // nothing to do
        }

        public boolean remove(Object o) {
            return false;
        }

        public boolean removeAll(Collection c) {
            return false;
        }

        public boolean retainAll(Collection c) {
            return false;
        }
    };
    private Class<T> type;

    public Null(Class<T> type) {
        this.type=type;
    }


    /**
     *  Creates a factory for proxy instances that is nullable.
     * @param type the type implemented by the proxy
     * @return  the factory
     */
    public static <T> Null<T> nullable(Class<T> type){
        return new Null<T>(type);
    }


    public T build(){
        return build(new StandardProxyFactory());
    }

    /**
     * Generate a Null Object proxy for a specific type.
     * <p>
     * Note that the method will only return a proxy if it cannot handle the type itself or <code>null</code> if the
     * type cannot be proxied.
     * </p>
     *
     * @param proxyFactory the {@link ProxyFactory} in use
     * @return object, proxy or <code>null</code>
     * @see com.thoughtworks.proxy.toys.nullobject
     * @since 0.1
     */


    public T build(ProxyFactory proxyFactory) {
        final Object result;

        // Primitives
        if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            result = Boolean.FALSE;
        } else if (byte.class.equals(type) || Byte.class.equals(type)) {
            result = (byte) 0;
        } else if (char.class.equals(type) || Character.class.equals(type)) {
            result = (char) 0;
        } else if (int.class.equals(type) || Integer.class.equals(type)) {
            result = 0;
        } else if (long.class.equals(type) || Long.class.equals(type)) {
            result = (long) 0;
        } else if (float.class.equals(type) || Float.class.equals(type)) {
            result = new Float(0.0);
        } else if (double.class.equals(type) || Double.class.equals(type)) {
            result = 0.0;
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
        return (T)result;

    }


    /**
     * Determine whether an object was created by {@link Null#object(Class)}.
     * 
     * @param object the object to examine
     * @return <code>true</code> if the object is a Null proxy.
     * @since 0.1
     */
    public static boolean isNullObject(final Object object) {
        return isNullObject(object, new StandardProxyFactory());
    }

    /**
     * Determine whether an object was created by {@link Null#object(Class, ProxyFactory)}.
     * 
     * @param object the object to examine
     * @param proxyFactory the {@link ProxyFactory} to use
     * @return <code>true</code> if the object is a Null proxy.
     * @since 0.1
     */
    public static boolean isNullObject(final Object object, final ProxyFactory proxyFactory) {
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

    private static boolean isNullProxyObject(final Object object, final ProxyFactory proxyFactory) {
        return proxyFactory.isProxyClass(object.getClass()) && proxyFactory.getInvoker(object) instanceof NullInvoker;
    }

}