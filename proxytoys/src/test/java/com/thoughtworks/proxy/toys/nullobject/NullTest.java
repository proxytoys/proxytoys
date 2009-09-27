/*
 * Created on 21-Mar-2004
 *
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.nullobject;

import com.thoughtworks.proxy.ProxyTestCase;
import static com.thoughtworks.proxy.toys.nullobject.Null.nullable;

import java.io.*;
import java.util.*;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author <a href="mailto:aslak@thoughtworks.com">Aslak Helles&oslash;y</a>
 */
public class NullTest extends ProxyTestCase {
    protected NullInvoker nil;

    protected void setUp() throws Exception {
        super.setUp();
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

    public void testShouldReturnDefaultValuesForPrimitives() throws Exception {
        // execute
        SomePrimitives nullObject = (SomePrimitives) nullable(SomePrimitives.class).build(getFactory());

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

    public void testShouldReturnEmptyArrayForArrayMethods() throws Exception {
        // execute
        SomeArrays nullObject = (SomeArrays) nullable(SomeArrays.class).build(getFactory());

        // verify
        assertEquals(0, nullObject.getIntArray().length);
        assertEquals(0, nullObject.getObjectArray().length);
    }

    public interface SomeCollections {
        Map getMap();

        List getList();

        Set getSet();

        SortedSet getSortedSet();

        SortedMap getSortedMap();
    }

    public void testShouldReturnStandardNullObjectsForCollections() throws Exception {
        // execute
        SomeCollections nullObject = (SomeCollections) nullable(SomeCollections.class).build(getFactory());

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

    public void testShouldExecuteVoidMethods() throws Exception {
        // execute
        InterfaceWithVoidMethod nullObject = nullable(
                InterfaceWithVoidMethod.class).build(getFactory());

        // verify
        nullObject.doVoidMethod();
    }

    public interface InterfaceWithObjectMethod {
        Object getObject();
    }

    public void testShouldNotReturnNullForMethodsThatReturnAnObject() throws Exception {
        // execute
        InterfaceWithObjectMethod nullObject = nullable(InterfaceWithObjectMethod.class).build(getFactory());

        // verify
        assertNotNull(nullObject.getObject());
    }

    public void testShouldRecogniseNullCollectionsAsNullObjects() throws Exception {
        assertTrue("Map", Null.isNullObject(nullable(Map.class).build( getFactory())));
        assertTrue("Set", Null.isNullObject(nullable(Set.class).build( getFactory())));
        assertTrue("List", Null.isNullObject(nullable(List.class).build( getFactory())));
        assertTrue("SortedSet", Null.isNullObject(nullable(SortedSet.class).build( getFactory())));
        assertTrue("SortedMap", Null.isNullObject(nullable(SortedMap.class).build( getFactory())));
        assertTrue("Object", Null.isNullObject(nullable(Object.class).build( getFactory())));
    }

    public void testShouldThrowUnsupportedOperationWhenAddToNullSortedSet() throws Exception {
        SortedSet sortedSet = (SortedSet) nullable(SortedSet.class).build(getFactory());
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

    public void testShouldIgnoreRemovingFromNullSortedSet() throws Exception {
        SortedSet sortedSet = (SortedSet) nullable(SortedSet.class).build(getFactory());
        Object object = new Object();

        sortedSet.clear();
        sortedSet.remove(object);
        sortedSet.removeAll(Collections.singleton(object));
        sortedSet.retainAll(Collections.singleton(object));
    }

    public void testShouldThrowUnsupportedOperationWhenAddingToNullSortedMap() throws Exception {
        SortedMap map = (SortedMap)nullable(SortedMap.class).build(getFactory());

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

    public void testShouldIgnoreRemvingFromMutatingNullSortedMap() throws Exception {
        SortedMap map = (SortedMap) nullable(SortedMap.class).build( getFactory());

        map.clear();
        map.remove("should fail");
    }

    public void testShouldReturnImmutableNullCollectionsForNullSortedMap() throws Exception {
        SortedMap map = (SortedMap) nullable(SortedMap.class).build( getFactory());

        assertSame(Collections.EMPTY_SET, map.keySet());
        assertSame(Collections.EMPTY_LIST, map.values());
        assertSame(Collections.EMPTY_SET, map.entrySet());
    }

    public interface InterfaceWithInterfaceMethod {
        InterfaceWithInterfaceMethod getSubInterface();
    }

    public void testShouldReturnNullObjectIfMethodReturnsAnInterface() throws Exception {
        // execute
        InterfaceWithInterfaceMethod nullObject = (InterfaceWithInterfaceMethod) nullable(
                InterfaceWithInterfaceMethod.class).build(getFactory());

        // verify
        assertNotNull(nullObject.getSubInterface());
        assertNotNull(nullObject.getSubInterface().getSubInterface());
    }

    public interface SimpleInterface {
    }

    public void testShouldCreateObjectWhichIsIdentifiableAsNullObject() throws Exception {
        // execute
        SimpleInterface nullObject = (SimpleInterface) nullable(SimpleInterface.class).build(getFactory());

        // verify
        assertTrue(Null.isNullObject(nullObject, getFactory()));
        assertFalse(Null.isNullObject(new Object()));
    }

    public void testShouldReturnNonNullStringForToStringMethod() throws Exception {
        // execute
        SimpleInterface nullObject = (SimpleInterface) nullable(SimpleInterface.class).build();

        // verify
        assertNotNull(nullObject.toString());
    }

    public void testShouldCompareEqualToAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(nullable(SimpleInterface.class).build(getFactory()), nullable(SimpleInterface.class).build( getFactory()));
    }

    private static void assertNotEquals(Object o1, Object o2) {
        assertFalse("Should be different", o1.equals(o2));
    }

    public void testShouldCompareUnequalToAnotherNullObjectOfDifferentType() throws Exception {
        assertNotEquals(nullable(Collection.class).build(getFactory()), nullable(List.class).build(getFactory()));
        assertNotEquals(nullable(List.class).build(getFactory()), nullable(Collection.class).build( getFactory()));
    }

    public void testShouldHaveSameHashcodeAsAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(nullable(Collection.class).build( getFactory()).hashCode(), nullable(Collection.class).build( getFactory()).hashCode());
    }

    private static void assertNotEquals(int i1, int i2) {
        assertFalse(i1 + ", " + i2 + " should be different", i1 == i2);
    }

    public void testShouldUsuallyHaveDifferentHashcodeFromAnotherNullObjectOfDifferentType() throws Exception {
        assertNotEquals(nullable(Collection.class).build( getFactory()).hashCode(), nullable(List.class).build( getFactory())
                .hashCode());
    }

    public void testShouldCreateEmptyStringForNullString() {
        assertEquals("", nullable(String.class).build( getFactory()));
    }

    public void testShouldCreateSameObjectForNullObject() {
        assertSame(nullable(Object.class).build( getFactory()), nullable(Object.class).build(getFactory()));
        assertNotNull(nullable(Object.class).build( getFactory()));
    }

    public interface ShouldSerialize extends Serializable {
        boolean test();
    }

    public interface ShouldNotSerialize {
    }

    public void testShouldNotBeSerializableIfInterfaceIsNotSerializable() throws Exception {
        // setup
        ShouldNotSerialize nullObject = (ShouldNotSerialize) nullable(ShouldNotSerialize.class).build( getFactory());
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

    public void testSerializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedProxy((ShouldSerialize) serializeWithJDK(nullable(ShouldSerialize.class).build( getFactory())));
    }

    public void testSerializeWithXStream() {
        useSerializedProxy((ShouldSerialize) serializeWithXStream(nullable(ShouldSerialize.class).build( getFactory())));
    }

    public void testSerializeWithXStreamInPureReflectionMode() {
        useSerializedProxy((ShouldSerialize) serializeWithXStreamAndPureReflection(nullable(
                ShouldSerialize.class).build(getFactory())));
    }
}
