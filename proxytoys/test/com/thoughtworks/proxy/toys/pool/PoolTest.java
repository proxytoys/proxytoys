/*
 * (c) 2004-2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.pool;

import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.kit.NoOperationResetter;
import com.thoughtworks.proxy.kit.Resetter;

import org.jmock.Mock;


/**
 * @author J&ouml;rg Schaible
 */
public class PoolTest extends ProxyTestCase {

    public static interface Identifiable {
        int getId();
    }

    public static class InstanceCounter implements Identifiable {
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

    private static class NotRetunringResetter implements Resetter {
        public boolean reset(Object object) {
            return false;
        }
    };

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
        final Pool pool = new Pool(Identifiable.class, new NoOperationResetter(), getFactory());
        pool.add(createIdentifiables(1));
        Identifiable borrowed = (Identifiable)pool.get();
        assertNotNull(borrowed);
        assertEquals(0, borrowed.getId());
    }

    public void testInstancesCanBeRecycled() {
        final Pool pool = new Pool(Identifiable.class, new NoOperationResetter(), getFactory());
        pool.add(createIdentifiables(3));
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
        final Pool pool = new Pool(Identifiable.class, new NoOperationResetter(), getFactory());
        try {
            pool.release(new InstanceCounter());
            fail("Thrown " + ClassCastException.class.getName() + " expected");
        } catch (final ClassCastException e) {
        }
    }

    public void testElementMustBeReturnedToOwnPool() {
        final Pool pool1 = new Pool(Identifiable.class, new NoOperationResetter(), getFactory());
        pool1.add(createIdentifiables(1));
        final Pool pool2 = new Pool(Identifiable.class, new NoOperationResetter(), getFactory());
        Object o1 = pool1.get();
        assertEquals(0, pool1.getAvailable());
        try {
            pool2.release(o1);
            fail("Thrown " + IllegalArgumentException.class.getName() + " expected");
        } catch (final IllegalArgumentException e) {
        }
        assertEquals(0, pool2.getAvailable());
    }

    public void testPoolReturnsNullIfExhausted() {
        final Pool pool = new Pool(Identifiable.class, new NoOperationResetter(), getFactory());
        pool.add(createIdentifiables(1));
        Object obj1 = pool.get();
        assertNotNull(obj1);
        assertEquals(0, pool.getAvailable());
        assertNull(pool.get());
    }

    public void testPoolSizeIsConstant() {
        final Pool pool = new Pool(Identifiable.class, new NoOperationResetter(), getFactory());
        pool.add(createIdentifiables(3));
        assertEquals(3, pool.size());
        Object obj1 = pool.get();
        assertEquals(3, pool.size());
        Object obj2 = pool.get();
        assertEquals(3, pool.size());
        Object obj3 = pool.get();
        assertEquals(3, pool.size());
        assertNotNull(obj1);
        assertNotNull(obj2);
        assertNotNull(obj3);
    }

    public void testPoolGrowingManually() {
        final Pool pool = new Pool(Identifiable.class, new NoOperationResetter(), getFactory());
        pool.add(createIdentifiables(1));
        Object obj1 = pool.get();
        assertEquals(0, pool.getAvailable());
        pool.add(new InstanceCounter());
        Object obj2 = pool.get();
        assertNotNull(obj1);
        assertNotNull(obj2);
        assertEquals(0, pool.getAvailable());
        pool.add(createIdentifiables(3));
        assertEquals(3, pool.getAvailable());
        assertEquals(5, pool.size());
    }

    public void testReturnedElementWillNotReturnToPoolIfExhausted() throws Exception {
        final Pool pool = new Pool(Identifiable.class, new NotRetunringResetter(), getFactory());
        pool.add(createIdentifiables(1));
        Object borrowed = pool.get();
        assertEquals(0, pool.getAvailable());
        assertEquals(1, pool.size());
        ((Poolable)borrowed).returnInstanceToPool();
        assertEquals(0, pool.getAvailable());
        assertEquals(0, pool.size());
    }

    public void testGarbageCollectedElementWillNotReturnToPoolIfExhausted() throws Exception {
        final Pool pool = new Pool(Identifiable.class, new NotRetunringResetter(), getFactory());
        pool.add(createIdentifiables(1));
        Object borrowed = pool.get();
        assertEquals(0, pool.getAvailable());
        assertEquals(1, pool.size());
        borrowed = null;
        System.gc();
        assertEquals(0, pool.getAvailable());
        assertEquals(0, pool.size());
    }

    public void testReturnedElementIsResetted() throws Exception {
        final Mock mockResetter = mock(Resetter.class);
        mockResetter.expects(once()).method("reset").will(returnValue(true));

        final Pool pool = new Pool(Identifiable.class, (Resetter)mockResetter.proxy(), getFactory());
        pool.add(createIdentifiables(1));
        Object borrowed = pool.get();
        ((Poolable)borrowed).returnInstanceToPool();
    }

    public void testGarbageCollectedElementIsResetted() throws Exception {
        final Mock mockResetter = mock(Resetter.class);
        mockResetter.expects(once()).method("reset").will(returnValue(true));

        final Pool pool = new Pool(Identifiable.class, (Resetter)mockResetter.proxy(), getFactory());
        pool.add(createIdentifiables(1));
        Object borrowed = pool.get();
        assertNotNull(borrowed);
        borrowed = null;
        System.gc();
        assertEquals(1, pool.getAvailable());
    }
}
