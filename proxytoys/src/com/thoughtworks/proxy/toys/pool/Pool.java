/*
 * Created on 01-Jul-2004
 * 
 * (c) 2004-2005 ThoughtWorks
 *
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.pool;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.InvokerReference;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.Resetter;
import com.thoughtworks.proxy.kit.SimpleReference;
import com.thoughtworks.proxy.toys.delegate.Delegating;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple pool implementation that collects its unused components of a specific type automatically.
 * <p>
 * The pool will only manage instances that were explicitly passed into the pool before. For more sophisticated pooling
 * strategies, derive from this class or wrap it.
 * </p>
 * <p>
 * The implementation will provide these instances wrapped by a proxy, that will return the instance automatically to
 * the pool, if it falls out of scope and is collected by the garbage collector. Since the pool only returns instances
 * wrapped by a proxy that implements the {@link Poolable} interface, this can be used to release th instance manually
 * to the pool also. With an implementation of the {@link Resetter} interface each element's status can be reset or the
 * element can be dropped from the pool at all, if it is exhausted.
 * </p>
 * <p>
 * A client can use the pool's monitor for an improved synchronization. Everytime an object returns to the pool, all
 * waiting Threads of the monitor will be notified.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 0.2
 * @see com.thoughtworks.proxy.toys.pool
 */
public class Pool {
    private static final Method returnInstanceToPool;

    static {
        try {
            returnInstanceToPool = Poolable.class.getMethod("returnInstanceToPool", null);
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
    protected final List availableInstances = new ArrayList();
    /** The resetter of the pooled elements. */
    protected final Resetter resetter;

    /**
     * Construct an Pool using the {@link StandardProxyFactory}.
     * 
     * @param type the type of the instances
     * @param resetter the resetter of the pooled elements
     * @since 0.2
     */
    public Pool(final Class type, final Resetter resetter) {
        this(type, resetter, new StandardProxyFactory());
    }

    /**
     * Construct a populated Pool with a specific proxy factory.
     * 
     * @param type the type of the instances
     * @param resetter the resetter of the pooled elements
     * @param proxyFactory the proxy factory to use
     * @since 0.2
     */
    public Pool(final Class type, final Resetter resetter, final ProxyFactory proxyFactory) {
        this.types = new Class[]{type, Poolable.class};
        this.factory = proxyFactory;
        this.resetter = resetter;
    }

    /**
     * Add a new instance as resource to the pool. The pool's monitor will be notified.
     * 
     * @param instance the new instance
     * @throws NullPointerException if instance is <code>null</code>
     * @since 0.2
     */
    public synchronized void add(final Object instance) {
        if (instance == null) {
            throw new NullPointerException();
        }
        availableInstances.add(new SimpleReference(instance));
        notifyAll();
    }

    /**
     * Add an array of new instances as resources to the pool. The pool's monitor will be notified.
     * 
     * @param instances the instances
     * @throws NullPointerException if instance is <code>null</code>
     * @since 0.2
     */
    public synchronized void add(final Object instances[]) {
        if (instances != null) {
            for (int i = 0; i < instances.length; ++i) {
                if (instances[i] == null) {
                    throw new NullPointerException();
                }
                availableInstances.add(new SimpleReference(instances[i]));
            }
            notifyAll();
        }
    }

    /**
     * Get an instance from the pool. If no instance is immediately available, the method will check internally for
     * returned objects from the garbage collector. This can be foreced by calling {@link System#gc()} first.
     * 
     * @return an available instance from the pool or <em>null</em>.
     * @since 0.2
     */
    public synchronized Object get() {
        final Object result;
        if (availableInstances.size() > 0 || getAvailable() > 0) {
            final ObjectReference delegate = (ObjectReference)availableInstances.remove(0);
            result = new PoolingInvoker(factory, delegate, Delegating.STATIC_TYPING).proxy();
            final Object weakReference = new WeakReference(result);
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
     * @throws ClassCastException if object was not {@link Poolable}.
     * @throws IllegalArgumentException if the object was not from this pool.
     * @since 0.2
     */
    public void release(final Object object) {
        final Poolable poolable = (Poolable)object;
        final PoolingInvoker invoker = (PoolingInvoker)((InvokerReference)object).getInvoker();
        if (this != invoker.getPoolInstance()) {
            throw new IllegalArgumentException("Release object from different pool");
        }
        poolable.returnInstanceToPool();
    }

    /**
     * Return the number of available instances of the pool. The method will also try to collect any pool instance that
     * was freed by the garbage collector. This can be foreced by calling {@link System#gc()} first. The pool's monitor
     * will be notified, if any object was collected and the {@link Resetter} retunred the object.
     * 
     * @return the number of available instances.
     * @since 0.2
     */
    public synchronized int getAvailable() {
        if (busyInstances.size() > 0) {
            final List freedInstances = new ArrayList();
            for (final Iterator iter = busyInstances.keySet().iterator(); iter.hasNext();) {
                final Object target = iter.next();
                final WeakReference ref = (WeakReference)busyInstances.get(target);
                if (ref.get() == null) {
                    freedInstances.add(target);
                }
            }
            final List resettedInstances = new ArrayList();
            for (Iterator iter = freedInstances.iterator(); iter.hasNext();) {
                final Object element = iter.next();
                busyInstances.remove(element);
                if (resetter.reset(element)) {
                    resettedInstances.add(new SimpleReference(element));
                }
            }
            availableInstances.addAll(resettedInstances);
            if (resettedInstances.size() > 0) {
                notifyAll();
            }
        }
        return availableInstances.size();
    }

    /**
     * Retrieve the number of instances managed by the pool.
     * 
     * @return the number of instances.
     * @since 0.2
     */
    public synchronized int size() {
        return availableInstances.size() + busyInstances.size();
    }

    private synchronized void returnInstanceToPool(final ObjectReference reference) {
        busyInstances.remove(reference.get());
        if (resetter.reset(reference.get())) {
            availableInstances.add(reference);
            notifyAll();
        }
    }

    /**
     * The {@link com.thoughtworks.proxy.Invoker} of the proxy.
     * 
     * @since 0.2
     */
    protected class PoolingInvoker extends DelegatingInvoker {

        /**
         * Construct a PoolingInvoker.
         * 
         * @param proxyFactory the {@link ProxyFactory} to use
         * @param delegateReference the {@link ObjectReference} with the delegate
         * @param staticTyping {@link Delegating#STATIC_TYPING} or {@link Delegating#DYNAMIC_TYPING}
         * @since 0.2
         */
        protected PoolingInvoker(ProxyFactory proxyFactory, ObjectReference delegateReference, boolean staticTyping) {
            super(proxyFactory, delegateReference, staticTyping);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result;
            if (method.equals(returnInstanceToPool)) {
                result = returnInstanceToPool();
            } else
                result = super.invoke(proxy, method, args);
            return result;
        }

        /**
         * Return the current instance to the pool. The pool's monitor will be notified, if the {@link Resetter} returns
         * the object.
         * 
         * @return {@link Void#TYPE}
         * @since 0.2
         */
        public Object returnInstanceToPool() {
            Pool.this.returnInstanceToPool(delegateReference);
            return Void.TYPE;
        }

        /**
         * Create a proxy for the types of the pool.
         * 
         * @return the new proxy instance
         * @since 0.2
         */
        protected Object proxy() {
            return proxyFactory.createProxy(types, this);
        }

        private Pool getPoolInstance() {
            return Pool.this;
        }
    }
}
