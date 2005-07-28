/*
 * Created on 11-May-2004
 *
 * (c) 2003-2005 ThoughtWorks
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.kit;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.InvokerReference;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Helper class for introspecting interface and class hierarchies.
 * 
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public class ReflectionUtils {
    /**
     * the {@link Object#equals(Object)} method.
     */
    public static final Method equals;
    /**
     * the {@link Object#hashCode()} method.
     */
    public static final Method hashCode;
    /**
     * the {@link Object#toString()} method.
     */
    public static final Method toString;

    static {
        try {
            equals = Object.class.getMethod("equals", new Class[]{Object.class});
            hashCode = Object.class.getMethod("hashCode", null);
            toString = Object.class.getMethod("toString", null);
        } catch (NoSuchMethodException e) {
            // /CLOVER:OFF
            throw new InternalError();
            // /CLOVER:ON
        } catch (SecurityException e) {
            // /CLOVER:OFF
            throw new InternalError();
            // /CLOVER:ON
        }
    }

    /**
     * Constructor. Do not call, it is a factory.
     */
    private ReflectionUtils() {
    }

    /**
     * Get all the interfaces implemented by a list of objects.
     * 
     * @param objects the list of objects to consider.
     * @return an set of interfaces. The set may be empty
     */
    public static Set getAllInterfaces(final Object[] objects) {
        final Set interfaces = new HashSet();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                getInterfaces(objects[i].getClass(), interfaces);
            }
        }
        interfaces.remove(InvokerReference.class);
        return interfaces;
    }

    /**
     * Get all interfaces of the given type. If the type is a class, the returned set contains any interface, that is
     * implemented by the class. If the type is an interface, the all superinterfaces and the interface itself are included.
     * 
     * @param clazz type to explore.
     * @return a {@link Set} with all interfaces. The set may be empty.
     */
    public static Set getAllInterfaces(final Class clazz) {
        final Set interfaces = new HashSet();
        getInterfaces(clazz, interfaces);
        interfaces.remove(InvokerReference.class);
        return interfaces;
    }

    private static void getInterfaces(Class clazz, final Set interfaces) {
        if (clazz.isInterface()) {
            interfaces.add(clazz);
        }
        // Class.getInterfaces will return only the interfaces that are
        // implemented by the current class. Therefore we must loop up
        // the hierarchy for the superclasses and the superinterfaces.
        while (clazz != null) {
            final Class[] implemented = clazz.getInterfaces();
            for (int i = 0; i < implemented.length; i++) {
                if (!interfaces.contains(implemented[i])) {
                    getInterfaces(implemented[i], interfaces);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Get most common superclass for all given objects.
     * 
     * @param objects the array of objects to consider.
     * @return the superclass or <code>{@link Void Void.class}</code> for an empty array.
     */
    public static Class getMostCommonSuperclass(final Object[] objects) {
        Class clazz = null;
        boolean found = false;
        if (objects != null && objects.length > 0) {
            while (!found) {
                for (int i = 0; i < objects.length; i++) {
                    found = true;
                    if (objects[i] != null) {
                        final Class currentClazz = objects[i].getClass();
                        if (clazz == null) {
                            clazz = currentClazz;
                        }
                        if (!clazz.isAssignableFrom(currentClazz)) {
                            if (currentClazz.isAssignableFrom(clazz)) {
                                clazz = currentClazz;
                            } else {
                                clazz = clazz.getSuperclass();
                                found = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (clazz == null) {
            clazz = Object.class;
        }
        return clazz;
    }

    /**
     * Add the given type to the given interfaces, if the given ProxyFactory supports proxy generation for this type.
     * 
     * @param clazz the class type (<code>Object.class</code> will be ignored)
     * @param interfaces the array of interfaces
     * @param proxyFactory the {@link ProxyFactory} in use
     */
    public static void addIfClassProxyingSupportedAndNotObject(
            final Class clazz, final Set interfaces, final ProxyFactory proxyFactory) {
        if (proxyFactory.canProxy(clazz) && !clazz.equals(Object.class)) {
            interfaces.add(clazz);
        }
    }

    /**
     * Convert the collection of class types to an array of class types.
     * 
     * @param collection with class types
     * @return an array of class types
     */
    public static Class[] toClassArray(final Collection collection) {
        return (Class[])collection.toArray(new Class[collection.size()]);
    }
}
