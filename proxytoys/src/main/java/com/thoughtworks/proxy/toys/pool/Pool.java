/*
 * Created on 01-Jul-2004
 * 
 * (c) 2004-2005 ThoughtWorks
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.pool;

import static com.thoughtworks.proxy.toys.delegate.DelegationMode.DIRECT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.InvokerReference;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.NoOperationResetter;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.Resetter;
import com.thoughtworks.proxy.kit.SimpleReference;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;
import com.thoughtworks.proxy.toys.delegate.DelegationMode;

/**
 * A simple pool implementation that collects its unused components of a specific type automatically.
 * <p>
 * The pool will only manage instances that were explicitly passed into the pool before. For more sophisticated pooling
 * strategies, derive from this class or wrap it.
 * </p>
 * <p>
 * The implementation will provide these instances wrapped by a proxy, that will return the instance automatically to
 * the pool, if it falls out of scope and is collected by the garbage collector. Since the pool only returns instances
 * wrapped by a proxy that implements the {@link Poolable} interface, this can be used to release the instance manually
 * to the pool also. With an implementation of the {@link Resetter} interface each element's status can be reset or the
 * element can be dropped from the pool at all, if it is exhausted.
 * </p>
 * <p>
 * A client can use the pool's monitor for an improved synchronization. Every time an object is returned to the pool, all
 * waiting Threads of the monitor will be notified. This notification will happen independently of the result of the
 * {@link Resetter#reset(Object)} method.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.proxy.toys.pool
 */
public class Pool<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Method returnInstanceToPool;

    static {
        try {
            returnInstanceToPool = Poolable.class.getMethod("returnInstanceToPool");
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    private Class<?> types[];
    private ProxyFactory factory;
    private transient Map<T, WeakReference<T>> busyInstances;
    private transient List<ObjectReference<T>> availableInstances;
    private Resetter<T> resetter;
    private SerializationMode serializationMode = SerializationMode.STANDARD;

    /**
     * Construct a populated Pool with a specific proxy factory
     *
     * @param type     the type of the instances
     * @param resetter the resetter of the pooled elements
     * @return return the pool with parameters specified
     */
    public static <T> PoolWith<T> poolable(Class<T> type, Resetter<T> resetter) {
        return new PoolWith<T>(new Pool<T>(type, resetter));
    }

    /**
     * Construct a populated Pool with a specific proxy factory
     *
     * @param type     the type of the instances
     * @return return the pool with parameters specified
     */
    public static <T> PoolWith<T> poolable(Class<T> type) {
        return new PoolWith<T>(new Pool<T>(type, new NoOperationResetter<T>()));
    }
    
    public static class PoolBuild<T> {

        protected Pool<T> pool;

        private PoolBuild(Pool<T> pool) {
            this.pool = pool;
        }

        /**
         * Build the pool using the {@link StandardProxyFactory}.
         *
         * @return the pool with predefined instances
         */
        public Pool<T> build() {
            return build(new StandardProxyFactory());
        }

        /**
         * Build the pool using a special {@link ProxyFactory}.
         *
         * @param factory the proxy factory to use
         * @return the pool with predefined instances
         */
        public Pool<T> build(ProxyFactory factory) {
            pool.factory = factory;
            return pool;
        }
    }

    public static class PoolWith<T> {
        private Pool<T> pool;

        private PoolWith(Pool<T> pool) {
            this.pool = pool;
        }

        public PoolModeOrBuild<T> with(T... instances) {
            pool.add(instances);
            return new PoolModeOrBuild<T>(pool);
        }

        public PoolModeOrBuild<T> withNoInstances() {
            return new PoolModeOrBuild<T>(pool);
        }

    }

    public static class PoolModeOrBuild<T> extends PoolBuild<T> {

        private PoolModeOrBuild(Pool<T> pool) {
            super(pool);
        }

        /**
         * Specify the serializationMode
         * <ul>
         * <li>STANDARD: the standard mode, i.e. all elements of the pool are also serialized and a
         * {@link NotSerializableException} may thrown</li>
         * <li>NONE: no element of the pool is also serialized and it must be populated again after
         * serialization</li>
         * <li>FORCE: all element of the pool are serialized, if possible. Otherwise the pool is
         * empty after serialization and must be populated again.</li>
         * </ul>
         *
         * @param serializationMode
         * @return the pool with a certain serialization mode
         * @throws IllegalArgumentException if the serialization mode is not one of the predefined values
         */

        public PoolBuild<T> mode(SerializationMode serializationMode) {
            pool.serializationMode = serializationMode;
            return new PoolBuild<T>(pool);
        }
    }
    
    /**
     * Construct a populated Pool with a specific proxy factory.
     *
     * @param type         the type of the instances
     * @param resetter     the resetter of the pooled elements

     */
    private Pool(final Class<T> type, final Resetter<T> resetter) {
        this();
        this.types = new Class[]{type, Poolable.class};
        this.resetter = resetter;
    }

    private Pool() {
        busyInstances = new HashMap<T, WeakReference<T>>();
        availableInstances = new ArrayList<ObjectReference<T>>();
    }

    /**
     * Add an array of new instances as resources to the pool. The pool's monitor will be notified.
     *
     * @param instances the instances
     * @throws NullPointerException if instance is <code>null</code>

     */
    public synchronized void add(final T... instances) {
        if (instances != null) {
            for (T instance : instances) {
                if (instance == null) {
                    throw new NullPointerException();
                }
                availableInstances.add(new SimpleReference<T>(instance));
            }
            notifyAll();
        }
    }

    /**
     * Get an instance from the pool. If no instance is immediately available, the method will check internally for
     * returned objects from the garbage collector. This can be forced by calling {@link System#gc()} first.
     *
     * @return an available instance from the pool or <em>null</em>.
     */
    public synchronized T get() {
        final T result;
        if (availableInstances.size() > 0 || getAvailable() > 0) {
            final ObjectReference<T> delegate = availableInstances.remove(0);
            result = new PoolingInvoker<T>(this, factory, delegate, DIRECT).proxy();
            final WeakReference<T> weakReference = new WeakReference<T>(result);
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
     * @throws ClassCastException       if object was not {@link Poolable}.
     * @throws IllegalArgumentException if the object was not from this pool.
     */
    public void release(final T object) {
        final Poolable poolable = Poolable.class.cast(object);
        @SuppressWarnings("unchecked")
        final PoolingInvoker<T> invoker = PoolingInvoker.class.cast(InvokerReference.class.cast(poolable).getInvoker());
        if (this != invoker.getPoolInstance()) {
            throw new IllegalArgumentException("Release object from different pool");
        }
        poolable.returnInstanceToPool();
    }

    /**
     * Return the number of available instances of the pool. The method will also try to collect any pool instance that
     * was freed by the garbage collector. This can be forced by calling {@link System#gc()} first. The pool's monitor
     * will be notified, if any object was collected and the {@link Resetter} returned the object.
     *
     * @return the number of available instances.
     */
    public synchronized int getAvailable() {
        if (busyInstances.size() > 0) {
            final List<T> freedInstances = new ArrayList<T>();
            for (final T target : busyInstances.keySet()) {
                final WeakReference<T> ref = busyInstances.get(target);
                if (ref.get() == null) {
                    freedInstances.add(target);
                }
            }
            final List<ObjectReference<T>> resettedInstances = new ArrayList<ObjectReference<T>>();
            for (final T element : freedInstances) {
                busyInstances.remove(element);
                if (resetter.reset(element)) {
                    resettedInstances.add(new SimpleReference<T>(element));
                }
            }
            availableInstances.addAll(resettedInstances);
            if (freedInstances.size() > 0) {
                notifyAll();
            }
        }
        return availableInstances.size();
    }

    /**
     * Retrieve the number of instances managed by the pool.
     *
     * @return the number of instances.
     */
    public synchronized int size() {
        return availableInstances.size() + busyInstances.size();
    }

    private synchronized void returnInstanceToPool(final ObjectReference<T> reference) {
        busyInstances.remove(reference.get());
        if (resetter.reset(reference.get())) {
            availableInstances.add(reference);
        }
        notifyAll();
    }

    private synchronized void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        final List<ObjectReference<T>> instances = new ArrayList<ObjectReference<T>>(availableInstances);
        Iterator<T> iter;
        for (iter = busyInstances.keySet().iterator(); iter.hasNext();) {
            instances.add(new SimpleReference<T>(iter.next()));
        }
        SerializationMode mode = serializationMode;
        if (mode == SerializationMode.FORCE) {
            try {
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final ObjectOutputStream testStream = new ObjectOutputStream(buffer);
                testStream.writeObject(instances); // force NotSerializableException
                testStream.close();
                mode = SerializationMode.STANDARD;
            } catch (final NotSerializableException e) {
                mode = SerializationMode.NONE;
            }
        }
        if (mode == SerializationMode.STANDARD) {
            out.writeObject(instances);
        } else {
            out.writeObject(new ArrayList<ObjectReference<T>>());
        }
    }

    private synchronized void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        @SuppressWarnings("unchecked")
        final List<ObjectReference<T>> list = List.class.cast(in.readObject());
        availableInstances = list;
        busyInstances = new HashMap<T, WeakReference<T>>();
    }

    /**
     * The {@link com.thoughtworks.proxy.Invoker} of the proxy.
     *

     */
    protected static class PoolingInvoker<T> extends DelegatingInvoker<T> {
        private static final long serialVersionUID = 1L;

        // explicit reference for serialization via reflection
        private Pool<T> pool;

        /**
         * Construct a PoolingInvoker.
         *
         * @param pool              the corresponding {@link Pool}
         * @param proxyFactory      the {@link ProxyFactory} to use
         * @param delegateReference the {@link ObjectReference} with the delegate
         * @param delegationMode    one of the {@linkplain DelegationMode delegation modes}
         */
        protected PoolingInvoker(
                Pool<T> pool, ProxyFactory proxyFactory, ObjectReference<T> delegateReference, DelegationMode delegationMode) {
            super(proxyFactory, delegateReference, delegationMode);
            this.pool = pool;
        }

        @Override
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
         */
        public Object returnInstanceToPool() {
            pool.returnInstanceToPool(getDelegateReference());
            return Void.TYPE;
        }

        /**
         * Create a proxy for the types of the pool.
         *
         * @return the new proxy instance
         */
        protected T proxy() {
            return getProxyFactory().<T>createProxy(this, pool.types);
        }

        private Pool<T> getPoolInstance() {
            return pool;
        }
    }
}
