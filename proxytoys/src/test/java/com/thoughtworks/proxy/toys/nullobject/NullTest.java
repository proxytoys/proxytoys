/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 21-Mar-2004
 */
package com.thoughtworks.proxy.toys.nullobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;


/**
 * @author Dan North
 * @author Aslak Helles&oslash;y
 */
public class NullTest extends AbstractProxyTest {
    protected NullInvoker nil;

    @Before
    public void setUp() throws Exception {
        nil = new NullInvoker(null, getFactory());
    }

    public interface SomePrimitives {
        boolean getBoolean();
        byte getByte();
        char getChar();
        int getInt();
        long getLong();
        float getFloat();
        double getDouble();
        Boolean getBoxedBoolean();
        Byte getBoxedByte();
        Character getBoxedChar();
        Integer getBoxedInt();
        Long getBoxedLong();
        Float getBoxedFloat();
        Double getBoxedDouble();
    }

    @Test
    public void shouldReturnDefaultValuesForPrimitives() throws Exception {
        // execute
        SomePrimitives nullObject = Null.proxy(SomePrimitives.class).build(getFactory());

        // verify
        assertNotNull(nullObject);
        assertEquals(false, nullObject.getBoolean());
        assertEquals(0, nullObject.getByte());
        assertEquals(0, nullObject.getChar());
        assertEquals(0, nullObject.getInt());
        assertEquals(0, nullObject.getLong());
        assertEquals(0.0, nullObject.getFloat(), 0.0);
        assertEquals(0.0, nullObject.getDouble(), 0.0);

        assertEquals(Boolean.FALSE, nullObject.getBoxedBoolean());
        assertEquals(new Byte((byte) 0), nullObject.getBoxedByte());
        assertEquals(new Character((char) 0), nullObject.getBoxedChar());
        assertEquals(new Integer(0), nullObject.getBoxedInt());
        assertEquals(new Long(0), nullObject.getBoxedLong());
        assertEquals(new Float(0.0), nullObject.getBoxedFloat());
        assertEquals(new Double(0.0), nullObject.getBoxedDouble());
    }

    public interface SomeArrays {
        int[] getIntArray();

        Object[] getObjectArray();
    }

    @Test
    public void shouldReturnEmptyArrayForArrayMethods() throws Exception {
        // execute
        SomeArrays nullObject = Null.proxy(SomeArrays.class).build(getFactory());

        // verify
        assertEquals(0, nullObject.getIntArray().length);
        assertEquals(0, nullObject.getObjectArray().length);
    }

    public interface SomeCollections {
        Map<?,?> getMap();
        List<?> getList();
        Set<?> getSet();
        SortedSet<?> getSortedSet();
        SortedMap<?,?> getSortedMap();
    }

    @Test
    public void shouldReturnStandardNullObjectsForCollections() throws Exception {
        // execute
        SomeCollections nullObject = Null.proxy(SomeCollections.class).build(getFactory());

        // verify
        assertSame(Collections.EMPTY_MAP, nullObject.getMap());
        assertSame(Collections.EMPTY_LIST, nullObject.getList());
        assertSame(Collections.EMPTY_SET, nullObject.getSet());
        assertSame(Null.NULL_SORTED_SET, nullObject.getSortedSet());
        assertSame(Null.NULL_SORTED_MAP, nullObject.getSortedMap());
    }

    public interface InterfaceWithVoidMethod {
        void doVoidMethod();
    }

    @Test
    public void shouldExecuteVoidMethods() throws Exception {
        // execute
        InterfaceWithVoidMethod nullObject = Null.proxy(
                InterfaceWithVoidMethod.class).build(getFactory());

        // verify
        nullObject.doVoidMethod();
    }

    public interface InterfaceWithObjectMethod {
        Object getObject();
    }

    @Test
    public void shouldNotReturnNullForMethodsThatReturnAnObject() throws Exception {
        // execute
        InterfaceWithObjectMethod nullObject = Null.proxy(InterfaceWithObjectMethod.class).build(getFactory());

        // verify
        assertNotNull(nullObject.getObject());
    }

    @Test
    public void shouldRecogniseNullCollectionsAsNullObjects() throws Exception {
        assertTrue("Map", Null.isNullObject(Null.proxy(Map.class).build(getFactory())));
        assertTrue("Set", Null.isNullObject(Null.proxy(Set.class).build(getFactory())));
        assertTrue("List", Null.isNullObject(Null.proxy(List.class).build(getFactory())));
        assertTrue("SortedSet", Null.isNullObject(Null.proxy(SortedSet.class).build(getFactory())));
        assertTrue("SortedMap", Null.isNullObject(Null.proxy(SortedMap.class).build(getFactory())));
        assertTrue("Object", Null.isNullObject(Null.proxy(Object.class).build(getFactory())));
    }

    @Test
    public void shouldThrowUnsupportedOperationWhenAddToNullSortedSet() throws Exception {
        @SuppressWarnings("unchecked")
        SortedSet<Object> sortedSet = Null.proxy(SortedSet.class).build(getFactory());
        Object object = new Object();

        try {
            sortedSet.add(object);
            fail("add");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            sortedSet.addAll(Collections.singleton(object));
            fail("addAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void shouldIgnoreRemovingFromNullSortedSet() throws Exception {
        @SuppressWarnings("unchecked")
        SortedSet<Object> sortedSet = Null.proxy(SortedSet.class).build(getFactory());
        Object object = new Object();

        sortedSet.clear();
        sortedSet.remove(object);
        sortedSet.removeAll(Collections.singleton(object));
        sortedSet.retainAll(Collections.singleton(object));
    }

    @Test
    public void shouldThrowUnsupportedOperationWhenAddingToNullSortedMap() throws Exception {
        @SuppressWarnings("unchecked")
        SortedMap<String, String> map = Null.proxy(SortedMap.class).build(getFactory());

        try {
            map.put("should fail", "really");
            fail("put");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            map.putAll(Collections.singletonMap("should fail", "really"));
            fail("putAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void shouldIgnoreRemvingFromMutatingNullSortedMap() throws Exception {
        @SuppressWarnings("unchecked")
        SortedMap<String, String> map = Null.proxy(SortedMap.class).build(getFactory());

        map.clear();
        map.remove("should fail");
    }

    @Test
    public void shouldReturnImmutableNullCollectionsForNullSortedMap() throws Exception {
        SortedMap<?,?> map = Null.proxy(SortedMap.class).build(getFactory());

        assertSame(Collections.EMPTY_SET, map.keySet());
        assertSame(Collections.EMPTY_LIST, map.values());
        assertSame(Collections.EMPTY_SET, map.entrySet());
    }

    public interface InterfaceWithInterfaceMethod {
        InterfaceWithInterfaceMethod getSubInterface();
    }

    @Test
    public void shouldReturnNullObjectIfMethodReturnsAnInterface() throws Exception {
        // execute
        InterfaceWithInterfaceMethod nullObject = Null.proxy(
                InterfaceWithInterfaceMethod.class).build(getFactory());

        // verify
        assertNotNull(nullObject.getSubInterface());
        assertNotNull(nullObject.getSubInterface().getSubInterface());
    }

    public interface SimpleInterface {
    }

    @Test
    public void shouldCreateObjectWhichIsIdentifiableAsNullObject() throws Exception {
        // execute
        SimpleInterface nullObject = Null.proxy(SimpleInterface.class).build(getFactory());

        // verify
        assertTrue(Null.isNullObject(nullObject, getFactory()));
        assertFalse(Null.isNullObject(new Object()));
    }

    @Test
    public void shouldReturnNonNullStringForToStringMethod() throws Exception {
        // execute
        SimpleInterface nullObject = Null.proxy(SimpleInterface.class).build();

        // verify
        assertNotNull(nullObject.toString());
    }

    @Test
    public void shouldCompareEqualToAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(Null.proxy(SimpleInterface.class).build(getFactory()), Null.proxy(SimpleInterface.class).build(getFactory()));
    }

    private static void assertNotEquals(Object o1, Object o2) {
        assertFalse("Should be different", o1.equals(o2));
    }

    @Test
    public void shouldCompareUnequalToAnotherNullObjectOfDifferentType() throws Exception {
        assertNotEquals(Null.proxy(Collection.class).build(getFactory()), Null.proxy(List.class).build(getFactory()));
        assertNotEquals(Null.proxy(List.class).build(getFactory()), Null.proxy(Collection.class).build(getFactory()));
    }

    @Test
    public void shouldHaveSameHashcodeAsAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(Null.proxy(Collection.class).build(getFactory()).hashCode(), Null.proxy(Collection.class).build(getFactory()).hashCode());
    }

    private static void assertNotEquals(int i1, int i2) {
        assertFalse(i1 + ", " + i2 + " should be different", i1 == i2);
    }

    @Test
    public void shouldUsuallyHaveDifferentHashcodeFromAnotherNullObjectOfDifferentType() throws Exception {
        assertNotEquals(Null.proxy(Collection.class).build(getFactory()).hashCode(), Null.proxy(List.class).build(getFactory())
                .hashCode());
    }

    @Test
    public void shouldCreateEmptyStringForNullString() {
        assertEquals("", Null.proxy(String.class).build(getFactory()));
    }

    @Test
    public void shouldCreateSameObjectForNullObject() {
        assertSame(Null.proxy(Object.class).build(getFactory()), Null.proxy(Object.class).build(getFactory()));
        assertNotNull(Null.proxy(Object.class).build(getFactory()));
    }

    public interface ShouldSerialize extends Serializable {
        boolean test();
    }

    public interface ShouldNotSerialize {
    }

    @Test
    public void shouldNotBeSerializableIfInterfaceIsNotSerializable() throws Exception {
        // setup
        ShouldNotSerialize nullObject = Null.proxy(ShouldNotSerialize.class).build(getFactory());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);

        // execute
        try {
            out.writeObject(nullObject);
            fail("Should throw exception");
        } catch (IOException expected) {
        }

        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
            in.readObject();
            fail("Should throw exception");
        } catch (IOException expected) {
        }
    }

    private void useSerializedProxy(ShouldSerialize serialized) {
        assertFalse(serialized.test());
        assertTrue("is Null Object", Null.isNullObject(serialized));
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedProxy(serializeWithJDK(Null.proxy(ShouldSerialize.class).build(getFactory())));
    }

    @Test
    public void serializeWithXStream() {
        useSerializedProxy(serializeWithXStream(Null.proxy(ShouldSerialize.class).build(getFactory())));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        useSerializedProxy(serializeWithXStreamAndPureReflection(Null.proxy(ShouldSerialize.class).build(getFactory())));
    }
}
