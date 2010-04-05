/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24-Feb-2005
 */
package com.thoughtworks.proxy.toys.dispatch;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;


/**
 * @author J&ouml;rg Schaible
 * @author Tianshuo Deng
 */
public class DispatchingTest extends AbstractProxyTest {

    public interface Foo {
        String getSomething();
    }

    public interface Bar {
        void doSomething(String s);
    }

    public interface FooMimic {
        String getSomething();
    }

    public interface BarSimilar {
        void doSomething(int i);
    }

    public interface FooBar extends Foo, Bar {
    }

    @Test
    public void callsAreDispatchedBetweenObjects() throws Exception {
        Foo fooMock = mock(Foo.class);
        Bar barMock = mock(Bar.class);

        Object foobar = Dispatching.proxy(Foo.class, Bar.class).with(
                fooMock, barMock).build(getFactory());

        when(fooMock.getSomething()).thenReturn("some thing");
        assertEquals("some thing", ((Foo) foobar).getSomething());
        ((Bar) foobar).doSomething("some thing");

        //verify
        verify(fooMock).getSomething();
        verify(barMock).doSomething(eq("some thing"));


    }

    // This is a proxy limitation ... unfortunately
    @Test
    public void onlyFirstOfMethodsWithSameSignaturesIsCalled() throws Exception {
        //setup
        Foo fooMock = mock(Foo.class);
        FooMimic fooMimicMock = mock(FooMimic.class);

        Object foo = Dispatching.proxy(Foo.class, FooMimic.class).with(
                fooMock, fooMimicMock).build(getFactory());
        //expects
        when(fooMock.getSomething()).thenReturn("some thing");
        when(fooMimicMock.getSomething()).thenReturn("some thing");
        //verify
        assertEquals("some thing", ((Foo) foo).getSomething());
        assertEquals("some thing", (fooMimicMock).getSomething());
        verify(fooMock).getSomething();
        verify(fooMimicMock).getSomething();
    }

    @Test
    public void methodsWithSameNameButDifferentSignatureAreDistinct() throws Exception {
        //setup
        Bar barMock = mock(Bar.class);
        BarSimilar barSimilarMock = mock(BarSimilar.class);

        Object bar = Dispatching.proxy(Bar.class, BarSimilar.class).with(
                barMock, barSimilarMock).build(getFactory());
        //expects

        ((Bar) bar).doSomething("some thing");
        ((BarSimilar) bar).doSomething(1);
        //verify

        verify(barMock).doSomething(eq("some thing"));
        verify(barSimilarMock).doSomething(eq(1));
    }

    @Test
    public void oneDelegateCanMatchMultipleTypes() throws Exception {
        //setup
        FooBar fooBarMock = mock(FooBar.class);

        Object foobar = Dispatching.proxy(Foo.class, Bar.class).with(fooBarMock).build(getFactory());
        //expects

        when(fooBarMock.getSomething()).thenReturn("some thing");

        //verify
        assertEquals("some thing", ((Foo) foobar).getSomething());
        ((Bar) foobar).doSomething("some thing");
        verify(fooBarMock).doSomething(eq("some thing"));
        verify(fooBarMock).getSomething();
    }

    @Test
    public void allTypesMustBeMatchedByOneDelegate() throws Exception {
        Foo fooMock = mock(Foo.class);

        try {
            Dispatching.proxy(Foo.class, Bar.class).with(fooMock).build(getFactory());
            fail("DispatchingException expected");
        } catch (final DispatchingException e) {
            assertEquals(Bar.class, e.getType());
        }
    }

    @Test
    public void hashCodeIsDifferentForEachProxy() throws Exception {
        Foo fooMock = mock(Foo.class);

        Object proxy1 = Dispatching.proxy(Foo.class).with(fooMock).build(getFactory());
        Object proxy2 = Dispatching.proxy(Foo.class).with(fooMock).build(getFactory());

        assertFalse(proxy1.hashCode() == proxy2.hashCode());
    }

    @Test
    public void stringRepresentationContainsImplementedTypes() throws Exception {
        FooBar fooBarMock = mock(FooBar.class);

        Object foobar = Dispatching.proxy(Foo.class, Bar.class).with(fooBarMock).build(getFactory());

        String string = foobar.toString();
        assertTrue(string.indexOf(Foo.class.getName()) >= 0);
        assertTrue(string.indexOf(Bar.class.getName()) >= 0);
    }

    @Test
    public void twoProxiesAreEqualIfSameTypesAreDelegatedToEqualDelegates() throws Exception {
        Object proxy1 = Dispatching.proxy(Comparable.class, Runnable.class, List.class).with(
            new ArrayList<String>(), "Hello", Thread.currentThread()).build(getFactory());
        Object proxy2 = Dispatching.proxy(List.class, Runnable.class, Comparable.class).with(
                "Hello", new ArrayList<String>(), Thread.currentThread()).build(getFactory());

        assertEquals(proxy1, proxy2);
    }

    @Test
    public void twoProxiesAreNotEqualIfSameTypesAreDelegatedToAtLeastOneNonEqualDelegate() throws Exception {
        Object proxy1 = Dispatching.proxy(Comparable.class, Runnable.class, List.class).with(
                new ArrayList<String>(), "Foo", Thread.currentThread()).build(getFactory());
        Object proxy2 = Dispatching.proxy(List.class, Runnable.class, Comparable.class).with(
                "Bar", new ArrayList<String>(), Thread.currentThread()).build(getFactory());

        assertFalse(proxy1.equals(proxy2));
    }


    private void useSerializedProxy(CharSequence sequence) {
        assertEquals("Test", sequence.toString());
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedProxy(serializeWithJDK(Dispatching.proxy(CharSequence.class).with("Test").build(getFactory())));
    }

    @Test
    public void serializeWithXStream() {
        useSerializedProxy(serializeWithXStream(Dispatching.proxy(CharSequence.class).with("Test").build(getFactory())));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        useSerializedProxy(serializeWithXStreamAndPureReflection(
                Dispatching.proxy(CharSequence.class).with("Test").build(getFactory())));
    }
}
