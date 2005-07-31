package com.thoughtworks.proxy.kit;

import junit.framework.TestCase;

import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServicesListener;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.Collection;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;


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

    public void testMostCommonSuperclassForClassesAreInSameHierarchy() throws IOException {
        assertEquals(OutputStreamWriter.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new FileWriter(FileDescriptor.out), new OutputStreamWriter(System.out)}));
        assertEquals(OutputStreamWriter.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), new FileWriter(FileDescriptor.out)}));
    }

    public void testMostCommonSuperclassForClassesInSameOrDifferentHierarchy() throws IOException {
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
                new Integer(1), new OutputStreamWriter(System.out)}));
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{
                new OutputStreamWriter(System.out), new Integer(1)}));
    }

    public void testMostCommonSuperclassForEmptyArray() {
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{}));
    }

    public void testMostCommonSuperclassForNullElements() {
        assertEquals(Object.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{null, null}));
    }

    public void testMostCommonSuperclassForCollections() {
        assertEquals(AbstractList.class, ReflectionUtils.getMostCommonSuperclass(new Object[]{new LinkedList(), new Vector()}));
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
        Method appendChar = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{new Character('c')});
        Method appendCharArray = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{new char[]{'c'}});
        Method appendShort = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{new Short((short)0)});
        Method appendObject = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{this});
        Method appendObject2 = ReflectionUtils.getMatchingMethod(StringBuffer.class, "append", new Object[]{new Exception()});
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
                    StringBuffer.class.getName() + ".append(" + this.getClass().getName() + ", " + Class.class.getName() + ")") >= 0);
        }
    }
}