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
}