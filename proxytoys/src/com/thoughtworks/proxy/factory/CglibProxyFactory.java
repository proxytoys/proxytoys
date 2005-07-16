/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.factory;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.nullobject.Null;

import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A {@link com.thoughtworks.proxy.ProxyFactory} based on <a href="http://cglib.sourceforge.net/">CGLIB</a>.
 * 
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public class CglibProxyFactory extends AbstractProxyFactory {
    private ProxyFactory standardProxyFactory = new StandardProxyFactory();
    // @todo Make thread-safe.
    // Keeps track of what is currently being created - to avoid infinite recursion
    private List creating = new ArrayList();

    /** The native invocation handle. */
    class CGLIBInvocationHandlerAdapter extends CoincidentalInvocationHandlerAdapter implements InvocationHandler {
        public CGLIBInvocationHandlerAdapter(Invoker invoker) {
            super(invoker);
        }
    }

    private static final Map boxedClasses = new HashMap();

    static {
        boxedClasses.put(Boolean.TYPE, Boolean.class);
        boxedClasses.put(Integer.TYPE, Integer.class);
        boxedClasses.put(Byte.TYPE, Byte.class);
        boxedClasses.put(Short.TYPE, Short.class);
        boxedClasses.put(Long.TYPE, Long.class);
        boxedClasses.put(Double.TYPE, Double.class);
        boxedClasses.put(Character.TYPE, Character.class);
        boxedClasses.put(Float.TYPE, Float.class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: If any type the proxy instance must fullfill are all interfaces, the factory will currently create a proxy based on
     * the JDK.
     * </p>
     */
    public Object createProxy(final Class[] types, final Invoker invoker) {
        final Class clazz = getSingleClass(types);
        if (clazz == null) {
            // slightly faster
            return standardProxyFactory.createProxy(types, invoker);
        }
        final Class[] interfaces = getInterfaces(types);
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setInterfaces(interfaces);
        enhancer.setCallback(new CGLIBInvocationHandlerAdapter(invoker));
        try {
            return enhancer.create();
        } catch (CodeGenerationException e) { // cglib 2.0
        } catch (IllegalArgumentException e) { // cglib 2.0.2
        } catch (NoSuchMethodError e) {
        }
        return createWithConstructor(clazz, enhancer);
    }

    private Class[] getInterfaces(final Class[] types) {
        final List interfaces = new ArrayList(Arrays.asList(types));
        for (final Iterator iterator = interfaces.iterator(); iterator.hasNext();) {
            final Class clazz = (Class)iterator.next();
            if (!clazz.isInterface()) {
                iterator.remove();
            }
        }
        interfaces.add(InvokerReference.class);
        return (Class[])interfaces.toArray(new Class[interfaces.size()]);
    }

    private Class getSingleClass(final Class[] types) {
        for (int i = 0; i < types.length; i++) {
            final Class type = types[i];
            if (!type.isInterface()) {
                return type;
            }
        }
        return null;
    }

    private Object createWithConstructor(final Class type, final Enhancer enhancer) {
        final Constructor constructor = getConstructor(type);
        final Class[] params = constructor.getParameterTypes();
        final Object[] args = new Object[params.length];
        for (int i = 0; i < args.length; i++) {
            if (!creating.contains(params[i])) {
                creating.add(params[i]);
                args[i] = Null.object(params[i], this);
                creating.remove(params[i]);
            } else {
                args[i] = null;
            }
        }
        final Object result = enhancer.create(params, args);
        return result;
    }

    private Constructor getConstructor(final Class type) {
        Constructor constructor = null;
        try {
            constructor = type.getConstructor(null);
        } catch (NoSuchMethodException e) {
            constructor = type.getConstructors()[0];

        }
        return constructor;
    }

    public boolean canProxy(final Class type) {
        int modifiers = type.getModifiers();
        return !Modifier.isFinal(modifiers);
    }

    public boolean isProxyClass(final Class type) {
        return Factory.class.isAssignableFrom(type)
                || (!type.equals(Object.class) && Proxy.isProxyClass(type))
                || standardProxyFactory.isProxyClass(type);
    }
}