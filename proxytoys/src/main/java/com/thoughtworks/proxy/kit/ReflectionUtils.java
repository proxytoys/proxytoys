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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            hashCode = Object.class.getMethod("hashCode");
            toString = Object.class.getMethod("toString");
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
        for (Object object : objects) {
            if (object != null) {
                getInterfaces(object.getClass(), interfaces);
            }
        }
        interfaces.remove(InvokerReference.class);
        return interfaces;
    }

    /**
     * Get all interfaces of the given type. If the type is a class, the returned set contains any interface, that is
     * implemented by the class. If the type is an interface, the all superinterfaces and the interface itself are
     * included.
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
            for (Class anImplemented : implemented) {
                if (!interfaces.contains(anImplemented)) {
                    getInterfaces(anImplemented, interfaces);
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
                for (Object object : objects) {
                    found = true;
                    if (object != null) {
                        final Class currentClazz = object.getClass();
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
     * Add the given type to the set of interfaces, if the given ProxyFactory supports proxy generation for this type.
     *
     * @param clazz        the class type (<code>Object.class</code> will be ignored)
     * @param interfaces   the set of interfaces
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
        return (Class[]) collection.toArray(new Class[collection.size()]);
    }

    /**
     * Get the method of the given type, that has matching parameter types to the given arguments.
     *
     * @param type       the type
     * @param methodName the name of the method to search
     * @param args       the arguments to match
     * @return the matching {@link Method}
     * @throws NoSuchMethodException if no matching {@link Method} exists
     * @since 0.2
     */
    public static Method getMatchingMethod(final Class type, final String methodName, final Object[] args)
            throws NoSuchMethodException {
        final Object[] newArgs = args == null ? new Object[0] : args;
        final Method[] methods = type.getMethods();
        final Set possibleMethods = new HashSet();
        Method method = null;
        for (int i = 0; method == null && i < methods.length; i++) {
            if (methodName.equals(methods[i].getName())) {
                final Class[] argTypes = methods[i].getParameterTypes();
                if (argTypes.length == newArgs.length) {
                    boolean exact = true;
                    Method possibleMethod = methods[i];
                    for (int j = 0; possibleMethod != null && j < argTypes.length; j++) {
                        final Class newArgType = newArgs[j] != null ? newArgs[j].getClass() : Object.class;
                        if ((argTypes[j].equals(byte.class) && newArgType.equals(Byte.class))
                                || (argTypes[j].equals(char.class) && newArgType.equals(Character.class))
                                || (argTypes[j].equals(short.class) && newArgType.equals(Short.class))
                                || (argTypes[j].equals(int.class) && newArgType.equals(Integer.class))
                                || (argTypes[j].equals(long.class) && newArgType.equals(Long.class))
                                || (argTypes[j].equals(float.class) && newArgType.equals(Float.class))
                                || (argTypes[j].equals(double.class) && newArgType.equals(Double.class))
                                || (argTypes[j].equals(boolean.class) && newArgType.equals(Boolean.class))) {
                            exact = true;
                        } else if (!argTypes[j].isAssignableFrom(newArgType)) {
                            possibleMethod = null;
                            exact = false;
                        } else if (!argTypes[j].isPrimitive()) {
                            if (!argTypes[j].equals(newArgType)) {
                                exact = false;
                            }
                        }
                    }
                    if (exact) {
                        method = possibleMethod;
                    } else if (possibleMethod != null) {
                        possibleMethods.add(possibleMethod);
                    }
                }
            }
        }
        if (method == null && possibleMethods.size() > 0) {
            method = (Method) possibleMethods.iterator().next();
        }
        if (method == null) {
            final StringBuffer name = new StringBuffer(type.getName());
            name.append('.');
            name.append(methodName);
            name.append('(');
            for (int i = 0; i < newArgs.length; i++) {
                if (i != 0) {
                    name.append(", ");
                }
                name.append(newArgs[i].getClass().getName());
            }
            name.append(')');
            throw new NoSuchMethodException(name.toString());
        }
        return method;
    }

    /**
     * Write a {@link Method} into an {@link ObjectOutputStream}.
     *
     * @param out    the stream
     * @param method the {@link Method} to write
     * @throws IOException if writing causes a problem
     * @since 1.2
     */
    public static void writeMethod(final ObjectOutputStream out, final Method method) throws IOException {
        out.writeObject(method.getDeclaringClass());
        out.writeObject(method.getName());
        out.writeObject(method.getParameterTypes());
    }

    /**
     * Read a {@link Method} from an {@link ObjectInputStream}.
     *
     * @param in the stream
     * @return the read {@link Method}
     * @throws IOException            if reading causes a problem
     * @throws ClassNotFoundException if class types from objects of the InputStream cannot be found
     * @throws InvalidObjectException if the {@link Method} cannot be found
     * @since 1.2
     */
    public static Method readMethod(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final Class type = (Class) in.readObject();
        final String name = (String) in.readObject();
        final Class[] parameters = (Class[]) in.readObject();
        try {
            return type.getMethod(name, parameters);
        } catch (final NoSuchMethodException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }
}
