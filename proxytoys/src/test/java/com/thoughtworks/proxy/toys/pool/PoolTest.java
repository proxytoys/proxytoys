/*
 * (c) 2004, 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01-Jul-2004
 */
package com.thoughtworks.proxy.toys.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.thoughtworks.proxy.AbstractProxyTest;
import com.thoughtworks.proxy.kit.Resetter;


/**
 * @author J&ouml;rg Schaible
 */
public class PoolTest extends AbstractProxyTest {

    public static interface Identifiable {
        int getId();
    }

    public static class InstanceCounter implements Identifiable, Serializable {
        private static final long serialVersionUID = 1L;
        private static int counter = 0;
        private int id;

        public InstanceCounter() {
            id = counter++;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(Object arg) {
            return arg instanceof Identifiable && id == ((Identifiable) arg).getId();
        }

        @Override
        public int hashCode()
        {
            return 31 * id;
        }
    }

    private static class NotReturningResetter implements Resetter<Identifiable> {
        public boolean reset(Identifiable object) {
            return false;
        }
    }

    private Identifiable[] createIdentifiables(int size) {
        final Identifiable array[] = new Identifiable[size];
        for (int i = 0; i < size; ++i) {
            array[i] = new InstanceCounter();
        }
        return array;
    }

    @Before
    public void setUp() throws Exception {
        InstanceCounter.counter = 0;
    }

    @Test
    public void instancesCanBeAccessed() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).build(getFactory());
        pool.add(createIdentifiables(1));
        Identifiable borrowed = pool.get();
        assertNotNull(borrowed);
        assertEquals(0, borrowed.getId());
    }

    @Test
    public void instancesCanBeAccessedUsingWithClauseOnBuilder() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).with(createIdentifiables(1)).build(getFactory());
        Identifiable borrowed = pool.get();
        assertNotNull(borrowed);
        assertEquals(0, borrowed.getId());
    }

    @Test
    public void instancesCanBeRecycled() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).build(getFactory());
        pool.add(createIdentifiables(3));
        Identifiable borrowed0 = pool.get();
        Identifiable borrowed1 = pool.get();
        Identifiable borrowed2 = pool.get();

        assertNotSame(borrowed0, borrowed1);
        assertNotSame(borrowed1, borrowed2);

        borrowed1 = null;
        System.gc();

        Identifiable borrowed = pool.get();
        assertEquals(1, borrowed.getId());

        Poolable.class.cast(borrowed).returnInstanceToPool();

        Object borrowedReloaded = pool.get();
        assertEquals(borrowed, borrowedReloaded);
    }

    @Test
    public void sizeIsConstant() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).build(getFactory());
        pool.add(createIdentifiables(3));

        assertEquals(3, pool.size());
        Identifiable borrowed0 = pool.get();
        assertEquals(3, pool.size());
        Identifiable borrowed1 = pool.get();
        assertEquals(3, pool.size());
        Identifiable borrowed2 = pool.get();
        assertEquals(3, pool.size());

        // keep instance
        assertNotNull(borrowed0);
        assertNotNull(borrowed1);
        assertNotNull(borrowed2);
    }

    @Test
    public void unmanagedInstanceCannotBeReleased() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).build(getFactory());
        try {
            pool.release(new InstanceCounter());
            fail("Thrown " + ClassCastException.class.getName() + " expected");
        } catch (final ClassCastException e) {
        }
    }

    @Test
    public void elementMustBeReturnedToOwnPool() {
        final Pool<Identifiable> pool1 = Pool.create(Identifiable.class).build(getFactory());
        pool1.add(createIdentifiables(1));
        final Pool<Identifiable> pool2 = Pool.create(Identifiable.class).build(getFactory());
        Identifiable o1 = pool1.get();
        assertEquals(0, pool1.getAvailable());
        try {
            pool2.release(o1);
            fail("Thrown " + IllegalArgumentException.class.getName() + " expected");
        } catch (final IllegalArgumentException e) {
        }
        assertEquals(0, pool2.getAvailable());
    }

    @Test
    public void poolReturnsNullIfExhausted() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).build(getFactory());
        pool.add(createIdentifiables(1));
        Identifiable obj1 = pool.get();
        assertNotNull(obj1);
        assertEquals(0, pool.getAvailable());
        assertNull(pool.get());
    }

    @Test
    public void poolSizeIsConstant() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).build(getFactory());
        pool.add(createIdentifiables(3));
        assertEquals(3, pool.size());
        Identifiable obj1 = pool.get();
        assertEquals(3, pool.size());
        Identifiable obj2 = pool.get();
        assertEquals(3, pool.size());
        Identifiable obj3 = pool.get();
        assertEquals(3, pool.size());
        assertNotNull(obj1);
        assertNotNull(obj2);
        assertNotNull(obj3);
    }

    @Test
    public void poolGrowingManually() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).build(getFactory());
        pool.add(createIdentifiables(1));
        Identifiable obj1 = pool.get();
        assertEquals(0, pool.getAvailable());
        pool.add(new InstanceCounter());
        Identifiable obj2 = pool.get();
        assertNotNull(obj1);
        assertNotNull(obj2);
        assertEquals(0, pool.getAvailable());
        pool.add(createIdentifiables(3));
        assertEquals(3, pool.getAvailable());
        assertEquals(5, pool.size());
    }

    @Test
    public void returnedElementWillNotReturnToPoolIfExhausted() throws Exception {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).resettedBy(new NotReturningResetter()).build(getFactory());
        pool.add(createIdentifiables(1));
        Identifiable borrowed = pool.get();
        assertEquals(0, pool.getAvailable());
        assertEquals(1, pool.size());
        ((Poolable) borrowed).returnInstanceToPool();
        assertEquals(0, pool.getAvailable());
        assertEquals(0, pool.size());
    }

    @Test
    public void garbageCollectedElementWillNotReturnToPoolIfExhausted() throws Exception {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).resettedBy(new NotReturningResetter()).build(getFactory());
        pool.add(createIdentifiables(1));
        Identifiable borrowed = pool.get();
        assertNotNull(borrowed);
        assertEquals(0, pool.getAvailable());
        assertEquals(1, pool.size());
        borrowed = null;
        System.gc();
        assertEquals(0, pool.getAvailable());
        assertEquals(0, pool.size());
    }

    @Test
    public void returnedElementIsResetted() throws Exception {
        @SuppressWarnings("unchecked")
        final Resetter<Identifiable> mockResetter = mock(Resetter.class);
        when(mockResetter.reset(Matchers.<Identifiable>anyObject())).thenReturn(true);
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).resettedBy(mockResetter).build(getFactory());
        pool.add(createIdentifiables(1));
        Identifiable borrowed = pool.get();
        ((Poolable) borrowed).returnInstanceToPool();

        verify(mockResetter).reset(Matchers.<Identifiable>anyObject());
    }

    @Test
    public void garbageCollectedElementIsResetted() throws Exception {
        @SuppressWarnings("unchecked")
        final Resetter<Identifiable> mockResetter = mock(Resetter.class);
        when(mockResetter.reset(Matchers.<Identifiable>anyObject())).thenReturn(true);
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).resettedBy(mockResetter).build(getFactory());
        pool.add(createIdentifiables(1));
        Identifiable borrowed = pool.get();
        assertNotNull(borrowed);
        borrowed = null;
        System.gc();
        assertEquals(1, pool.getAvailable());

        //verify
        verify(mockResetter).reset(Matchers.<Identifiable>anyObject());
    }

    private void twoItemsCanBeBorrowedFromPool(final Pool<?> pool) {
        assertEquals(2, pool.size());
        Object borrowed0 = pool.get();
        Object borrowed1 = pool.get();
        assertNotNull(borrowed0);
        assertNotNull(borrowed1);
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).with(createIdentifiables(2)).build(getFactory());
        Identifiable borrowed = pool.get();
        twoItemsCanBeBorrowedFromPool(serializeWithJDK(pool));
        assertNotNull(borrowed); // keep instance
    }

    @Test
    public void serializeWithXStream() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).with(createIdentifiables(2)).build(getFactory());
        Identifiable borrowed = pool.get();
        twoItemsCanBeBorrowedFromPool(serializeWithXStream(pool));
        assertNotNull(borrowed); // keep instance
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).with(createIdentifiables(2)).build(getFactory());
        Identifiable borrowed = pool.get();
        twoItemsCanBeBorrowedFromPool(serializeWithXStreamAndPureReflection(pool));
        assertNotNull(borrowed); // keep instance
    }

    @Test
    public void forcedSerializationWithJDK() throws IOException, ClassNotFoundException {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).with(createIdentifiables(2)).mode(SerializationMode.FORCE).build(getFactory());
        Identifiable borrowed = pool.get();
        twoItemsCanBeBorrowedFromPool(serializeWithJDK(pool));
        assertNotNull(borrowed); // keep instance
    }

    @Test
    public void forcedSerializationWithXStream() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).with(createIdentifiables(2)).mode(SerializationMode.FORCE).build(getFactory());
        Identifiable borrowed = pool.get();
        twoItemsCanBeBorrowedFromPool(serializeWithXStream(pool));
        assertNotNull(borrowed); // keep instance
    }

    @Test
    public void forcedSerializationWithXStreamInPureReflectionMode() {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).with(createIdentifiables(2)).mode(SerializationMode.FORCE).build(getFactory());
        Identifiable borrowed = pool.get();
        twoItemsCanBeBorrowedFromPool(serializeWithXStreamAndPureReflection(pool));
        assertNotNull(borrowed); // keep instance
    }

    @Test
    public void forcedSerializationWithUnserializableObject() throws IOException, ClassNotFoundException {
        final Pool<NotSerializable> pool = Pool.create(NotSerializable.class).mode(SerializationMode.FORCE).build(getFactory());
        NotSerializable notSerializable = new NotSerializable();
        pool.add(notSerializable);
        final Pool<NotSerializable> serialized = serializeWithJDK(pool);
        assertEquals(0, serialized.size());
        serialized.add(notSerializable);
        assertEquals(1, serialized.size());
    }

    @Test
    public void forcedSerializationWithEmptyPool() throws IOException, ClassNotFoundException {
        final Pool<Identifiable> pool = Pool.create(Identifiable.class).mode(SerializationMode.NONE).build(getFactory());
        pool.add(createIdentifiables(2));
        final Pool<Identifiable> serialized = serializeWithJDK(pool);
        assertEquals(0, serialized.size());
    }
    
    public static class NotSerializable {
        String not = "not";
    }
}
