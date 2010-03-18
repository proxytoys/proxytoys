package com.thoughtworks.proxy.kit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServicesListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.junit.Test;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class ReflectionUtilsTest {
    @Test
    public void mostCommonSuperclassForClassesWithACommonBaseClass() {
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new StringWriter(), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), new StringWriter()}));
    }

    @Test
    public void mostCommonSuperclassForClassesAreInSameHierarchy() {
        assertEquals(OutputStreamWriter.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new FileWriter(FileDescriptor.out), new OutputStreamWriter(System.out)}));
        assertEquals(OutputStreamWriter.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), new FileWriter(FileDescriptor.out)}));
    }

    @Test
    public void mostCommonSuperclassForClassesInSameOrDifferentHierarchy() {
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

    @Test
    public void mostCommonSuperclassForUnmatchingObjects() {
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                1, new OutputStreamWriter(System.out)}));
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), 1}));
    }

    @Test
    public void mostCommonSuperclassForEmptyArray() {
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{}));
    }

    @Test
    public void mostCommonSuperclassForNullElements() {
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{null, null}));
    }

    @Test
    public void mostCommonSuperclassForCollections() {
        assertEquals(AbstractList.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new LinkedList<Object>(), new Vector<Object>()}));
    }

    @Test
    public void allInterfacesOfListShouldBeFound() {
        Set<Class<?>> interfaces = ReflectionUtils.getAllInterfaces(BeanContextServices.class);
        assertTrue(interfaces.contains(BeanContextServices.class));
        assertTrue(interfaces.contains(BeanContext.class));
        assertTrue(interfaces.contains(Collection.class));
        assertTrue(interfaces.contains(BeanContextServicesListener.class));
        assertTrue(interfaces.contains(EventListener.class));
    }

    @Test
    public void matchingMethodIsFound() throws Exception {
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

    @Test
    public void matchingMethodArgumentCanBeNull() throws Exception {
        Method appendObject = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{null});
        assertNotNull(appendObject);
    }

    @Test
    public void matchingMethodArgumentsCanBeNull() throws Exception {
        Method method = ReflectionUtils.getMatchingMethod(StringBuffer.class, "toString", null);
        assertNotNull(method);
    }

    @Test
    public void noSuchMethodExceptionIsThrownIfNoMatchingMethodCouldBeFound() throws Exception {
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

    @Test
    public void methodCanBeSerialized() throws IOException, ClassNotFoundException {
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

    @Test
    public void methodCanBeDeserialized() throws IOException, ClassNotFoundException {
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

    @Test
    public void unknownDeserializedMethodThrowsInvalidObjectException() throws IOException, ClassNotFoundException {
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