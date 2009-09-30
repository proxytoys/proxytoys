package com.thoughtworks.proxy.kit;

import junit.framework.TestCase;

import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServicesListener;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class ReflectionUtilsTest extends TestCase {
    public void testMostCommonSuperclassForClassesWithACommonBaseClass() {
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new StringWriter(), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), new StringWriter()}));
    }

    public void testMostCommonSuperclassForClassesAreInSameHierarchy() {
        assertEquals(OutputStreamWriter.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new FileWriter(FileDescriptor.out), new OutputStreamWriter(System.out)}));
        assertEquals(OutputStreamWriter.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), new FileWriter(FileDescriptor.out)}));
    }

    public void testMostCommonSuperclassForClassesInSameOrDifferentHierarchy() {
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new FileWriter(FileDescriptor.out), new StringWriter(), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new FileWriter(FileDescriptor.out), new OutputStreamWriter(System.out), new StringWriter()}));
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new StringWriter(), new FileWriter(FileDescriptor.out), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), new FileWriter(FileDescriptor.out), new StringWriter()}));
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new StringWriter(), new OutputStreamWriter(System.out), new FileWriter(FileDescriptor.out)}));
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), new StringWriter(), new FileWriter(FileDescriptor.out)}));
    }

    public void testMostCommonSuperclassForUnmatchingObjects() {
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                1, new OutputStreamWriter(System.out)}));
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), 1}));
    }

    public void testMostCommonSuperclassForEmptyArray() {
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{}));
    }

    public void testMostCommonSuperclassForNullElements() {
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{null, null}));
    }

    public void testMostCommonSuperclassForCollections() {
        assertEquals(AbstractList.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new LinkedList(), new Vector()}));
    }

    public void testAllInterfacesOfListShouldBeFound() {
        Set interfaces = ReflectionUtils.getAllInterfaces(BeanContextServices.class);
        assertTrue(interfaces.contains(BeanContextServices.class));
        assertTrue(interfaces.contains(BeanContext.class));
        assertTrue(interfaces.contains(Collection.class));
        assertTrue(interfaces.contains(BeanContextServicesListener.class));
        assertTrue(interfaces.contains(EventListener.class));
    }

    public void testMatchingMethodIsFound() throws Exception {
        Method appendChar = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{'c'});
        Method appendCharArray = ReflectionUtils.getMatchingMethod(
                StringBuffer.class, "append", new Object[]{new char[]{'c'}});
        Method appendShort = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{(short) 0});
        Method appendObject = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{this});
        Method appendObject2 = ReflectionUtils.getMatchingMethod(
                StringBuffer.class, "append", new Object[]{new Exception()});
        assertNotNull(appendChar);
        assertNotNull(appendCharArray);
        assertNotNull(appendShort);
        assertNotNull(appendObject);
        assertNotNull(appendObject2);
        assertNotSame(appendChar, appendCharArray);
        assertNotSame(appendObject, appendChar);
        assertNotSame(appendObject, appendCharArray);
        assertNotSame(appendObject, appendShort);
        assertTrue(appendObject.equals(appendObject2));
    }

    public void testMatchingMethodArgumentCanBeNull() throws Exception {
        Method appendObject = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{null});
        assertNotNull(appendObject);
    }

    public void testMatchingMethodArgumentsCanBeNull() throws Exception {
        Method method = ReflectionUtils.getMatchingMethod(StringBuffer.class, "toString", null);
        assertNotNull(method);
    }

    public void testNoSuchMethodExceptionIsThrownIfNoMatchingMethodCouldBeFound() throws Exception {
        try {
            ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{this, StringBuffer.class});
            fail("Thrown " + NoSuchMethodException.class.getName() + " expected");
        } catch (final NoSuchMethodException e) {
            assertTrue(e.getMessage().indexOf(
                    StringBuffer.class.getName()
                            + ".append("
                            + this.getClass().getName()
                            + ", "
                            + Class.class.getName()
                            + ")") >= 0);
        }
    }

    public void testMethodCanBeSerialized() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(outBuffer);
        ReflectionUtils.writeMethod(outStream, ReflectionUtils.equals);
        outStream.close();
        ByteArrayInputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
        ObjectInputStream inStream = new ObjectInputStream(inBuffer);
        assertSame(Object.class, inStream.readObject());
        assertEquals("equals", inStream.readObject());
        assertTrue(Arrays.equals(new Class[]{Object.class}, (Object[]) inStream.readObject()));
        inStream.close();
    }

    public void testMethodCanBeDeserialized() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(outBuffer);
        outStream.writeObject(Object.class);
        outStream.writeObject("equals");
        outStream.writeObject(new Class[]{Object.class});
        outStream.close();
        ByteArrayInputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
        ObjectInputStream inStream = new ObjectInputStream(inBuffer);
        assertEquals(ReflectionUtils.equals, ReflectionUtils.readMethod(inStream));
    }

    public void testUnknownDeserializedMethodThrowsInvalidObjectException() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(outBuffer);
        outStream.writeObject(Object.class);
        outStream.writeObject("equals");
        outStream.writeObject(new Class[0]);
        outStream.close();
        ByteArrayInputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
        ObjectInputStream inStream = new ObjectInputStream(inBuffer);
        try {
            ReflectionUtils.readMethod(inStream);
            fail("Thrown " + InvalidObjectException.class.getName() + " expected");
        } catch (final InvalidObjectException e) {
            // ok
        }
    }
}