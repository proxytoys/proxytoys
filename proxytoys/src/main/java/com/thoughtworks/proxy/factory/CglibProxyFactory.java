/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.nullobject.Null;

/**
 * A {@link com.thoughtworks.proxy.ProxyFactory} based on <a href="http://cglib.sourceforge.net/">CGLIB</a>.
 *
 * @author Aslak Helles&oslash;y
 * @see com.thoughtworks.proxy.factory
 */
public class CglibProxyFactory extends AbstractProxyFactory {
    private static final long serialVersionUID = -5615928639194345818L;
    private static final ThreadLocal<List<Class<?>>> cycleGuard = new ThreadLocal<List<Class<?>>>();
    private static final ProxyFactory standardProxyFactory = new StandardProxyFactory();

    /**
     * The native invocation handler.
     *
     */
    static class CGLIBInvocationHandlerAdapter extends CoincidentalInvocationHandlerAdapter implements InvocationHandler {
        private static final long serialVersionUID = 418834172207536454L;

        /**
         * Construct a CGLIBInvocationHandlerAdapter.
         *
         * @param invoker the wrapping invoker instance
         */
        public CGLIBInvocationHandlerAdapter(Invoker invoker) {
            super(invoker);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: If any type the proxy instance must fulfill are all interfaces, the factory will currently create a proxy
     * based on the JDK.
     * </p>
     *
     */
    
    public <T> T createProxy(final Invoker invoker, final Class<?>... types) {
        final Class<?> type = getSingleClass(types);
        if (type == null) {
            // slightly faster
            return standardProxyFactory.<T>createProxy(invoker, types);
        }
        final Class<?>[] interfaces = getInterfaces(types);
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setInterfaces(interfaces);
        enhancer.setCallback(new CGLIBInvocationHandlerAdapter(invoker));
        try {
            @SuppressWarnings("unchecked")
            final T proxy = (T)enhancer.create();
            return proxy;
        } catch (CodeGenerationException e) { // cglib 2.0
        } catch (IllegalArgumentException e) { // cglib 2.0.2
        } catch (NoSuchMethodError e) {
        }
        @SuppressWarnings("unchecked")
        final T proxy = (T)createWithConstructor(type, enhancer);
        return proxy;
    }

    private Class<?>[] getInterfaces(final Class<?>[] types) {
        final List<Class<?>> interfaces = new ArrayList<Class<?>>(Arrays.asList(types));
        for (final Iterator<Class<?>> iterator = interfaces.iterator(); iterator.hasNext();) {
            final Class<?> type = iterator.next();
            if (!type.isInterface()) {
                iterator.remove();
            }
        }
        interfaces.add(InvokerReference.class);
        return interfaces.toArray(new Class[interfaces.size()]);
    }

    private Class<?> getSingleClass(final Class<?>[] types) {
        for (final Class<?> type : types) {
            if (!type.isInterface()) {
                return type;
            }
        }
        return null;
    }

    private Object createWithConstructor(final Class<?> type, final Enhancer enhancer) {
        final Constructor<?> constructor = getConstructor(type);
        final Class<?>[] params = constructor.getParameterTypes();
        final Object[] args = new Object[params.length];
        if (cycleGuard.get() == null) {
            cycleGuard.set(new ArrayList<Class<?>>());
        }
        final List<Class<?>> creating = cycleGuard.get();
        for (int i = 0; i < args.length; i++) {
            if (!creating.contains(params[i])) {
                creating.add(params[i]);
                try {
                    args[i] = Null.proxy(params[i]).build(this);
                } finally {
                    creating.remove(params[i]);
                }
            } else {
                args[i] = null;
            }
        }
        return enhancer.create(params, args);
    }

    private Constructor<?> getConstructor(final Class<?> type) {
        try {
            return type.getConstructor(Class[].class.cast(null));
        } catch (NoSuchMethodException e) {
            return type.getConstructors()[0];
        }
    }

    public boolean canProxy(final Class<?> type) {
        return !Modifier.isFinal(type.getModifiers());
    }

    public boolean isProxyClass(final Class<?> type) {
        return Factory.class.isAssignableFrom(type)
                || (!type.equals(Object.class) && Proxy.isProxyClass(type))
                || standardProxyFactory.isProxyClass(type);
    }
}