/*
 * Created on 11-May-2004
 *
 * (c) 2003-2005 ThoughtWorks
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


/**
 * Helper class for introspecting interface and class hierarchies.
 * 
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @since 0.1
 * @deprecated since 0.2, use com.thoughtworks.proxy.kit.ReflectionUitls.
 */
public class ClassHierarchyIntrospector {
    /**
     * the {@link Object#equals(Object)} method.
     * 
     * @deprecated since 0.2, use com.thoughtworks.proxy.kit.ReflectionUitls.
     */
    public static Method equals;
    /**
     * the {@link Object#hashCode()} method.
     * 
     * @deprecated since 0.2, use com.thoughtworks.proxy.kit.ReflectionUitls.
     */
    public static Method hashCode;

    static {
        try {
            equals = Object.class.getMethod("equals", new Class[]{Object.class});
            hashCode = Object.class.getMethod("hashCode", null);
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

    private ClassHierarchyIntrospector() {
    }

    /**
     * Get all the interfaces implemented by a list of objects.
     * 
     * @param objects the list of objects to consider.
     * @return an array of interfaces.
     * @deprecated since 0.2, use com.thoughtworks.proxy.kit.ReflectionUitls.
     */
    public static Class[] getAllInterfaces(final Object[] objects) {
        Set interfaces = ReflectionUtils.getAllInterfaces(objects);
        return (Class[])interfaces.toArray(new Class[interfaces.size()]);
    }

    /**
     * Get all interfaces of the given type. If the type is a class, the returned list contains any interface, that is
     * implemented by the class. If the type is an interface, the all superinterfaces and the interface itself are included.
     * 
     * @param clazz type to explore.
     * @return an array with all interfaces. The array may be empty.
     * @deprecated since 0.2, use com.thoughtworks.proxy.kit.ReflectionUitls.
     */
    public static Class[] getAllInterfaces(final Class clazz) {
        Set interfaces = ReflectionUtils.getAllInterfaces(clazz);
        return (Class[])interfaces.toArray(new Class[interfaces.size()]);
    }

    /**
     * Get most common superclass for all given objects.
     * 
     * @param objects the array of objects to consider.
     * @return the superclass or <code>{@link Void}.class</code> for an empty array.
     * @deprecated since 0.2, use com.thoughtworks.proxy.kit.ReflectionUitls.
     */
    public static Class getMostCommonSuperclass(Object[] objects) {
        return ReflectionUtils.getMostCommonSuperclass(objects);
    }

    /**
     * Add the given type to the given interfaces, if the given ProxyFactory supports proxy generation for this type.
     * 
     * @param clazz the class type (<code>Object.class</code> will be ignored)
     * @param interfaces the array of interfaces
     * @param proxyFactory the {@link ProxyFactory} in use
     * @return the new array of interfaces including the class type (if can be proxied)
     * @deprecated since 0.2, use com.thoughtworks.proxy.kit.ReflectionUitls.
     */
    public static Class[] addIfClassProxyingSupportedAndNotObject(
            final Class clazz, final Class[] interfaces, final ProxyFactory proxyFactory) {
        Set types = new HashSet();
        ReflectionUtils.addIfClassProxyingSupportedAndNotObject(clazz, types, proxyFactory);
        return (Class[])types.toArray(new Class[types.size()]);
    }
}
