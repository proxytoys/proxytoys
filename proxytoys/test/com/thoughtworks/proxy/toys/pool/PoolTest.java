/*
 * (c) 2004-2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.pool;

import com.thoughtworks.proxy.ProxyTestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class PoolTest extends ProxyTestCase {

    static public interface Identifiable {
        int getId();
    }

    static public class InstanceCounter implements Identifiable {
        private static int counter = 0;
        final private int id;

        public InstanceCounter() {
            id = counter++;
        }

        public int getId() {
            return id;
        }

        public boolean equals(Object arg) {
            return arg instanceof Identifiable && id == ((Identifiable)arg).getId();
        }
    }

    private Object[] createIdentifiables(int size) {
        final Object array[] = new Object[size];
        for (int i = 0; i < size; ++i) {
            array[i] = new InstanceCounter();
        }
        return array;
    }

    protected void setUp() throws Exception {
        InstanceCounter.counter = 0;
    }

    public void testInstancesCanBeAccessed() {
        final Pool pool = new Pool(Identifiable.class, createIdentifiables(1), getFactory());
        Identifiable borrowed = (Identifiable)pool.get();
        assertNotNull(borrowed);
        assertEquals(0, borrowed.getId());
    }

    public void testInstancesCanBeRecycled() {
        final Pool pool = new Pool(Identifiable.class, createIdentifiables(3), getFactory());
        Object borrowed0 = pool.get();
        Object borrowed1 = pool.get();
        Object borrowed2 = pool.get();

        assertNotSame(borrowed0, borrowed1);
        assertNotSame(borrowed1, borrowed2);

        borrowed1 = null;
        System.gc();

        Identifiable borrowed = (Identifiable)pool.get();
        assertEquals(1, borrowed.getId());

        ((Poolable)borrowed).returnInstanceToPool();

        Object borrowedReloaded = pool.get();
        assertEquals(borrowed, borrowedReloaded);
    }

    public void testUnmanagedInstanceCannotBeReleased() {
        final Pool pool = new Pool(Identifiable.class);
        try {
            pool.release(new InstanceCounter());
            fail();
        } catch (ClassCastException e) {
        }
    }

    public void testElementWillReturnToOwnPool() {
        final Pool pool1 = new Pool(Identifiable.class, createIdentifiables(1), getFactory());
        final Pool pool2 = new Pool(Identifiable.class, createIdentifiables(1), getFactory());
        Object o1 = pool1.get();
        assertEquals(0, pool1.getAvailable());
        assertEquals(1, pool2.getAvailable());
        pool2.release(o1);
        assertEquals(1, pool1.getAvailable());
        assertEquals(1, pool2.getAvailable());
    }

    public void testPoolReturnsNullIfExhausted() {
        final Pool pool = new Pool(Identifiable.class, createIdentifiables(1), getFactory());
        Object obj1 = pool.get();
        assertNotNull(obj1);
        assertEquals(0, pool.getAvailable());
        assertNull(pool.get());
    }

    public void testPoolSizeIsConstant() {
        final Pool pool = new Pool(Identifiable.class, createIdentifiables(3), getFactory());
        assertEquals(3, pool.size());
        Object obj1 = pool.get();
        assertEquals(3, pool.size());
        Object obj2 = pool.get();
        assertEquals(3, pool.size());
        Object obj3 = pool.get();
        assertEquals(3, pool.size());
    }

    public void testPoolGrowingManually() {
        final Pool pool = new Pool(Identifiable.class, createIdentifiables(1), getFactory());
        Object obj1 = pool.get();
        assertEquals(0, pool.getAvailable());
        pool.add(new InstanceCounter());
        Object obj2 = pool.get();
        assertNotNull(obj2);
        assertEquals(0, pool.getAvailable());
        pool.add(createIdentifiables(3));
        assertEquals(3, pool.getAvailable());
        assertEquals(5, pool.size());
    }
}
