/*
 * (c) 2004-2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.pool;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;
import com.thoughtworks.proxy.toys.delegate.ObjectReference;
import com.thoughtworks.proxy.toys.delegate.SimpleReference;


/**
 * A simple pool implementation that collects its unused components automatically.
 * <p>
 * The pool will only manage instances of a type that were explicitly passed into the pool
 * before. For more sophisticated pooling strategies, derive from this class or wrap it.
 * </p>
 * <p>
 * The implementation will provide these instances wrapped by a proxy, that will return the
 * instance automatically, if it falls out of scope and would be collected by the garbage
 * collector. Because of the proxy every provided instance implements the {@link Poolable}
 * interface, that can be used to release th instance manually to the pool also.
 * </p>
 * <p>
 * Note, that the {@link StandardProxyFactory} is based on reflection and therefore only types
 * can be managed, that implements an interface.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public class Pool {
    private static final Method returnInstanceToPool;

    static {
        try {
            returnInstanceToPool = Poolable.class.getMethod("returnInstanceToPool", new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    /** The interfaces of the pooled instances. */
    protected final Class types[];
    /** The proxy factory. */
    protected final ProxyFactory factory;
    /** The busy instancess i.e. the ones currently in usage. */
    protected final Map busyInstances = new HashMap();
    /** The available instancess. */
    protected final List availableInstances = new LinkedList();

    /**
     * Construct an Pool with a specific proxy factory.
     * 
     * @param type the type of the instances
     * @param proxyFactory the proxy factory to use
     */
    public Pool(final Class type, final ProxyFactory proxyFactory) {
        this.types = new Class[]{type, Poolable.class};
        this.factory = proxyFactory;
    }

    /**
     * Construct an Pool with the {@link StandardProxyFactory}.
     * 
     * @param type the type of the instances
     */
    public Pool(final Class type) {
        this(type, new StandardProxyFactory());
    }

    /**
     * Construct a populated Pool with a specific proxy factory.
     * 
     * @param type the type of the instances
     * @param targets the instances to be managed
     * @param proxyFactory the proxy factory to use
     */
    public Pool(final Class type, final Object targets[], final ProxyFactory proxyFactory) {
        this(type, proxyFactory);
        add(targets);
    }

    /**
     * Construct an Pool with the {@link StandardProxyFactory}.
     * 
     * @param type the type of the instances
     * @param targets the instances to be managed
     */
    public Pool(final Class type, final Object targets[]) {
        this(type);
        add(targets);
    }

    /**
     * Add a new instance resource to the pool.
     * 
     * @param instance the new instance
     */
    public void add(final Object instance) {
        availableInstances.add(new SimpleReference(instance));
    }

    /**
     * Add an array of new instance resources to the pool.
     * 
     * @param instances the instances
     */
    public void add(final Object instances[]) {
        for (int i = 0; i < instances.length; ++i) {
            availableInstances.add(new SimpleReference(instances[i]));
        }
    }

    /**
     * @return Return an available instance from the pool or <em>null</em>.
     */
    public Object get() {
        final Object result;
        if (getAvailable() > 0) {
            final ObjectReference delegate = (ObjectReference) availableInstances.remove(0);
            result = new AutoPoolingInvoker(types, factory, delegate, true).proxy();
            Object weakReference = new WeakReference(result);
            busyInstances.put(delegate.get(), weakReference);
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Release a pool instance manually.
     * 
     * @param object the instance to release
     * @throws ClassCastException Thrown if object was not {@link Poolable}.
     */
    public void release(final Object object) {
        ((Poolable) object).returnInstanceToPool();
    }

    /**
     * Return the number of available instances of the pool. The method will also try to collect
     * any pool instance that was freed by the garbage collector.
     * 
     * @return Returns the number of available instances.
     */
    public int getAvailable() {
        if (busyInstances.size() > 0) {
            List freedInstances = new LinkedList();
            for (final Iterator iter = busyInstances.keySet().iterator(); iter.hasNext();) {
                Object target = iter.next();
                WeakReference ref = (WeakReference) busyInstances.get(target);
                if (ref.get() == null) {
                    freedInstances.add(new SimpleReference(target));
                }
            }
            for (Iterator iter = freedInstances.iterator(); iter.hasNext();) {
                busyInstances.remove(((ObjectReference) iter.next()).get());
            }
            availableInstances.addAll(freedInstances);
        }
        return availableInstances.size();
    }

    /**
     * @return Returns the number of instances managed by the pool.
     */
    public int size() {
        return availableInstances.size() + busyInstances.size();
    }

    private void returnInstanceToPool(final ObjectReference reference) {
        busyInstances.remove(reference.get());
        availableInstances.add(reference);
    }

    /**
     * The proxy invoker class.
     */
    protected class AutoPoolingInvoker extends DelegatingInvoker {
        private final Class[] types;

        protected AutoPoolingInvoker(
                Class[] types, ProxyFactory proxyFactory, ObjectReference delegateReference, boolean staticTyping) {
            super(proxyFactory, delegateReference, staticTyping);
            this.types = types;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result;
            if (method.equals(returnInstanceToPool)) {
                result = returnInstanceToPool();
            } else
                result = super.invoke(proxy, method, args);
            return result;
        }

        public Object returnInstanceToPool() {
            Pool.this.returnInstanceToPool(delegateReference);
            return Void.TYPE;
        }

        public Object proxy() {
            return proxyFactory.createProxy(types, this);
        }
    }
}
