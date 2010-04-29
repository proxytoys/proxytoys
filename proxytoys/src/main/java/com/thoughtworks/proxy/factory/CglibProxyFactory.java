/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03-May-2004
 */
package com.thoughtworks.proxy.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;

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
 * @since 0.1
 * @see com.thoughtworks.proxy.factory
 */
public class CglibProxyFactory extends AbstractProxyFactory {
	private static final long serialVersionUID = -5615928639194345818L;
    private static final ThreadLocal<List<Class<?>>> cycleGuard = new ThreadLocal<List<Class<?>>>();
    private static final ProxyFactory standardProxyFactory = new StandardProxyFactory();
    private transient ForeignPackageNamingPolicy namingPolicy = new ForeignPackageNamingPolicy();

    /**
     * The native invocation handler.
     *
     * @since 0.1
     */
    static class CGLIBInvocationHandlerAdapter extends CoincidentalInvocationHandlerAdapter implements InvocationHandler {
        private static final long serialVersionUID = 418834172207536454L;

        /**
         * Construct a CGLIBInvocationHandlerAdapter.
         *
         * @param invoker the wrapping invoker instance
         * @since 0.1
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
     */
    public <T> T createProxy(final Invoker invoker, final Class<?>... types) {
        final Class<?> type = getSingleClass(types);
        if (type == null) {
            // slightly faster
            return standardProxyFactory.<T>createProxy(invoker, types);
        }
        if (type.isPrimitive()) {
            throw new IllegalArgumentException("Cannot subclass primitive type");
        }
        final Class<?>[] interfaces = getInterfaces(types);
        final Enhancer enhancer = new Enhancer();
        for(;;) {
	        enhancer.setSuperclass(type);
	        enhancer.setInterfaces(interfaces);
	        enhancer.setCallback(new CGLIBInvocationHandlerAdapter(invoker));
	        try {
	            @SuppressWarnings("unchecked")
	            final T proxy = (T)enhancer.create();
	            return proxy;
	        } catch (CodeGenerationException e) { // cglib 2.0
				final Throwable wrapper = e.getCause();
				if (wrapper != null 
					&& wrapper.getCause() instanceof SecurityException 
					&& enhancer.getNamingPolicy() != namingPolicy) {
					enhancer.setNamingPolicy(namingPolicy);
					continue;
				}
	        } catch (IllegalArgumentException e) { // cglib 2.0.2
	        } catch (NoSuchMethodError e) {
	        }
	        break;
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
			Constructor<?>[] constructors = type.getConstructors();
			if (constructors.length == 0) {
				constructors = type.getDeclaredConstructors();
			}
			if (constructors.length == 0) {
				throw new IllegalArgumentException("Cannot create proxy for this type without declared constructor.");
			}
			return constructors[0];
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
    
	private static class ForeignPackageNamingPolicy extends DefaultNamingPolicy
	{
		@Override
		public String getClassName(final String prefix, final String source, final Object key, final Predicate names)
		{
			return getClass().getPackage().getName() + ".proxy." + super.getClassName(prefix, source, key, names);
		}
	}

	private Object readResolve()
	{
		namingPolicy = new ForeignPackageNamingPolicy();
		return this;
	}
}