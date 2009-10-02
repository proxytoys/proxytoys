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
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;
import com.thoughtworks.proxy.toys.delegate.DelegationMode;
import static com.thoughtworks.proxy.toys.delegate.DelegationMode.DIRECT;

import java.io.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;


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
 * A client can use the pool's monitor for an improved synchronization. Everytime an object is returned to the pool, all
 * waiting Threads of the monitor will be notified. This notification will happen independently of the result of the
 * {@link Resetter#reset(Object)} method.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.proxy.toys.pool
 * @since 0.2
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

    /**
     * <code>SERIALIZATION_FORCE</code> is the value for serialization of the pool with or without serializable
     * objects. If the obejcts cannot be serialized, the pool is emtpy after serialization und must be populated again.
     */
    public final static int SERIALIZATION_FORCE = 1;
    /**
     * <code>SERIALIZATION_STANDARD</code> is the value for the standard serialization of the pool with its objects.
     * If the obejcts cannot be serialized, a {@link NotSerializableException} is thrown.
     */
    public final static int SERIALIZATION_STANDARD = 0;
    /**
     * <code>SERIALIZATION_NONE</code> is the value for serialization of the pool without the objects. The pool is
     * emtpy after serialization und must be populated again.
     */
    public final static int SERIALIZATION_NONE = -1;

    private Class types[];
    private ProxyFactory factory;
    private transient Map busyInstances;
    private transient List availableInstances;
    private Resetter resetter;
    private int serializationMode;

    /**
     * Construct a populated Pool with a specific proxy factory
     *
     * @param type     the type of the instances
     * @param resetter the resetter of the pooled elements
     * @return return the pool with parameters specified
     * @since 0.2
     */
    public static <T> Pool<T> poolable(Class<T> type, Resetter<T> resetter) {
        return new Pool<T>(type, resetter);
    }

    /**
     * Build the pool.
     * @param factory the proxy factory to use
     * @return
     */
    public Pool<T> build(ProxyFactory factory) {
        this.factory = factory;
        return this;
    }

    /**
     * Specify the serializationMode
     * <ul>
     * <l>{@link #SERIALIZATION_STANDARD}: the standard mode, i.e. all elements of the pool are also serialized and a
     * {@link NotSerializableException} may thrown</li>
     * <li>{@link #SERIALIZATION_NONE}: no element of the pool is also serialized and it must be populated again after
     * serialization</li>
     * <li>{@link #SERIALIZATION_FORCE}: all element of the pool are serialized, if possible. Otherwise the pool is
     * empty after serialization and must be populated again.</li>
     * </ul>
     *
     * @param serializationMode
     * @return the pool with a certain serialization mode
     * @throws IllegalArgumentException if the serialization mode is not one of the predefined values
     */

    public Pool<T> mode(int serializationMode) {
        this.serializationMode = serializationMode;
        if (Math.abs(serializationMode) > 1) {
            throw new IllegalArgumentException("Invalid serialization mode");
        }
        return this;
    }


    /**
     * Construct a populated Pool with a specific proxy factory.
     *
     * @param type         the type of the instances
     * @param resetter     the resetter of the pooled elements
     * @since 0.2
     */
    private Pool(final Class type, final Resetter resetter) {
        this();
        this.types = new Class[]{type, Poolable.class};
        this.resetter = resetter;
    }


    private Pool() {
        busyInstances = new HashMap();
        availableInstances = new ArrayList();
    }

    /**
     * Add a new instance as resource to the pool. The pool's monitor will be notified.
     *
     * @param instance the new instance
     * @throws NullPointerException if instance is <code>null</code>
     * @since 0.2
     */
    public synchronized void add(final T instance) {
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
    public synchronized void add(final T instances[]) {
        if (instances != null) {
            for (T instance : instances) {
                if (instance == null) {
                    throw new NullPointerException();
                }
                availableInstances.add(new SimpleReference(instance));
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
    public synchronized T get() {
        final Object result;
        if (availableInstances.size() > 0 || getAvailable() > 0) {
            final ObjectReference delegate = (ObjectReference) availableInstances.remove(0);
            result = new PoolingInvoker(this, factory, delegate, DIRECT).proxy();
            final Object weakReference = new WeakReference(result);
            busyInstances.put(delegate.get(), weakReference);
        } else {
            result = null;
        }
        return (T) result;
    }

    /**
     * Release a pool instance manually.
     *
     * @param object the instance to release
     * @throws ClassCastException       if object was not {@link Poolable}.
     * @throws IllegalArgumentException if the object was not from this pool.
     * @since 0.2
     */
    public void release(final Object object) {
        final Poolable poolable = (Poolable) object;
        final PoolingInvoker invoker = (PoolingInvoker) ((InvokerReference) object).getInvoker();
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
            for (final Object target : busyInstances.keySet()) {
                final WeakReference ref = (WeakReference) busyInstances.get(target);
                if (ref.get() == null) {
                    freedInstances.add(target);
                }
            }
            final List resettedInstances = new ArrayList();
            for (final Object element : freedInstances) {
                busyInstances.remove(element);
                if (resetter.reset(element)) {
                    resettedInstances.add(new SimpleReference(element));
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
     * @since 0.2
     */
    public synchronized int size() {
        return availableInstances.size() + busyInstances.size();
    }

    private synchronized void returnInstanceToPool(final ObjectReference reference) {
        busyInstances.remove(reference.get());
        if (resetter.reset(reference.get())) {
            availableInstances.add(reference);
        }
        notifyAll();
    }

    private synchronized void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        final List instances = new ArrayList(availableInstances);
        Iterator iter;
        for (iter = busyInstances.keySet().iterator(); iter.hasNext();) {
            instances.add(new SimpleReference(iter.next()));
        }
        int mode = serializationMode;
        if (mode == SERIALIZATION_FORCE) {
            try {
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final ObjectOutputStream testStream = new ObjectOutputStream(buffer);
                testStream.writeObject(instances); // force NotSerializableException
                testStream.close();
                mode = SERIALIZATION_STANDARD;
            } catch (final NotSerializableException e) {
                mode = SERIALIZATION_NONE;
            }
        }
        if (mode == SERIALIZATION_STANDARD) {
            out.writeObject(instances);
        } else {
            out.writeObject(new ArrayList());
        }
    }

    private synchronized void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        availableInstances = (List) in.readObject();
        busyInstances = new HashMap();
    }

    /**
     * The {@link com.thoughtworks.proxy.Invoker} of the proxy.
     *
     * @since 0.2
     */
    protected static class PoolingInvoker extends DelegatingInvoker {
        private static final long serialVersionUID = 1L;

        // explicit reference for serialization via reflection
        private Pool pool;

        /**
         * Construct a PoolingInvoker.
         *
         * @param pool              the corresponding {@link Pool}
         * @param proxyFactory      the {@link ProxyFactory} to use
         * @param delegateReference the {@link ObjectReference} with the delegate
         * @param delegationMode    one of the {@linkplain DelegationMode delgation modes}
         * @since 0.2
         */
        protected PoolingInvoker(
                Pool pool, ProxyFactory proxyFactory, ObjectReference delegateReference, DelegationMode delegationMode) {
            super(proxyFactory, delegateReference, delegationMode);
            this.pool = pool;
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
            pool.returnInstanceToPool(getDelegateReference());
            return Void.TYPE;
        }

        /**
         * Create a proxy for the types of the pool.
         *
         * @return the new proxy instance
         * @since 0.2
         */
        protected Object proxy() {
            return getProxyFactory().createProxy(this, pool.types);
        }

        private Pool getPoolInstance() {
            return pool;
        }
    }
}
