package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.InvokerReference;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

/**
 * Helper class for introspecting interface and class hierarchies.
 *
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision: 1.1 $
 */
public class ClassHierarchyIntrospector {
    /**
     * the {@link Object#equals(Object)} method.
     */
    public static Method equals;
    /**
     * the {@link Object#hashCode()} method.
     */
    public static Method hashCode;

    static {
        try {
            equals = Object.class.getMethod("equals", new Class[]{Object.class});
            hashCode = Object.class.getMethod("hashCode", null);
        } catch (NoSuchMethodException e) {
            ///CLOVER:OFF
            throw new InternalError();
            ///CLOVER:ON
        } catch (SecurityException e) {
            ///CLOVER:OFF
            throw new InternalError();
            ///CLOVER:ON
        }
    }

    private ClassHierarchyIntrospector() {
    }

    /**
     * Get all the interfaces implemented by a list of objects.
     *
     * @param objects the list of objects to consider.
     * @return an array of interfaces.
     */
    public static Class[] getAllInterfaces(Object[] objects) {
        Set interfaces = new HashSet();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                getInterfaces(objects[i].getClass(), interfaces);
            }
        }
        return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    /**
     * Get all interfaces of the given type.
     * If the type is a class, the returned list contains any interface, that is
     * implemented by the class. If the type is an interface, the all
     * superinterfaces and the interface itself are included.
     *
     * @param clazz type to explore.
     * @return an array with all interfaces. The array may be empty.
     */
    public static Class[] getAllInterfaces(Class clazz) {
        Set interfaces = new HashSet();
        getInterfaces(clazz, interfaces);
        interfaces.remove(InvokerReference.class);
        return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    private static void getInterfaces(Class clazz, Set interfaces) {
        if (clazz.isInterface()) {
            interfaces.add(clazz);
        }
        // Class.getInterfaces will return only the interfaces that are 
        // implemented by the current class. Therefore we must loop up
        // the hierarchy for the superclasses and the superinterfaces. 
        while (clazz != null) {
            Class[] implemented = clazz.getInterfaces();
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
     * @return the superclass or <code>{@link Void}.class</code> for an empty array.
     */
    public static Class getMostCommonSuperclass(Object[] objects) {
        Class clazz = null;
        boolean found = false;
        if (objects != null && objects.length > 0) {
            while (!found) {
                for (int i = 0; i < objects.length; i++) {
                    found = true;
                    if (objects[i] != null) {
                        Class currentClazz = objects[i].getClass();
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

    public static Class[] addIfClassProxyingSupportedAndNotObject(Class clazz, Class[] interfaces, ProxyFactory proxyFactory) {
        Class[] result;
        if (proxyFactory.canProxy(ArrayList.class) && !clazz.equals(Object.class)) {
            result = new Class[interfaces.length + 1];
            result[0] = clazz;
            System.arraycopy(interfaces, 0, result, 1, interfaces.length);
        } else {
            result = interfaces;
        }
        return result;
    }
}
