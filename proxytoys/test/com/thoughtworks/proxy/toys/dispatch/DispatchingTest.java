/*
 * Created on 24-Feb-2005
 * 
 * (c) 2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.dispatch;

import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.kit.NoOperationResetter;
import com.thoughtworks.proxy.kit.Resetter;

import org.jmock.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author J&ouml;rg Schaible
 */
public class DispatchingTest extends ProxyTestCase {

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

    public void testCallsAreDispatchedBetweenObjects() throws Exception {
        Mock fooMock = mock(Foo.class);
        Mock barMock = mock(Bar.class);

        Object foobar = Dispatching.object(new Class[]{Foo.class, Bar.class}, new Object[]{
                fooMock.proxy(), barMock.proxy()}, getFactory());

        fooMock.expects(once()).method("getSomething").withNoArguments().will(returnValue("some thing"));
        barMock.expects(once()).method("doSomething").with(eq("some thing"));

        assertEquals("some thing", ((Foo)foobar).getSomething());
        ((Bar)foobar).doSomething("some thing");
    }

    // This is a proxy limitation ... unfortunately
    public void testOnlyFirstOfMethodsWithSameSignaturesIsCalled() throws Exception {
        Mock fooMock = mock(Foo.class);
        Mock fooMimicMock = mock(FooMimic.class);

        Object foo = Dispatching.object(new Class[]{Foo.class, FooMimic.class}, new Object[]{
                fooMock.proxy(), fooMimicMock.proxy()}, getFactory());

        fooMock.expects(once()).method("getSomething").withNoArguments().will(returnValue("some thing"));
        // should be fooMimicMock ...
        fooMock.expects(once()).method("getSomething").withNoArguments().will(returnValue("some thing"));

        assertEquals("some thing", ((Foo)foo).getSomething());
        assertEquals("some thing", ((FooMimic)foo).getSomething());
    }

    public void testMethodsWithSameNameButDifferentSignatureAreDistinct() throws Exception {
        Mock barMock = mock(Bar.class);
        Mock barSimilarMock = mock(BarSimilar.class);

        Object bar = Dispatching.object(new Class[]{Bar.class, BarSimilar.class}, new Object[]{
                barMock.proxy(), barSimilarMock.proxy()}, getFactory());

        barMock.expects(once()).method("doSomething").with(eq("some thing"));
        barSimilarMock.expects(once()).method("doSomething").with(eq(1));

        ((Bar)bar).doSomething("some thing");
        ((BarSimilar)bar).doSomething(1);
    }

    public void testOneDelegateCanMatchMultipleTypes() throws Exception {
        Mock fooBarMock = mock(FooBar.class);

        Object foobar = Dispatching.object(
                new Class[]{Foo.class, Bar.class}, new Object[]{fooBarMock.proxy()}, getFactory());

        fooBarMock.expects(once()).method("doSomething").with(eq("some thing"));
        fooBarMock.expects(once()).method("getSomething").withNoArguments().will(returnValue("some thing"));

        assertEquals("some thing", ((Foo)foobar).getSomething());
        ((Bar)foobar).doSomething("some thing");
    }

    public void testAllTypesMustBeMatchedByOneDelegate() throws Exception {
        Mock fooMock = mock(Foo.class);

        try {
            Dispatching.object(new Class[]{Foo.class, Bar.class}, new Object[]{fooMock.proxy()}, getFactory());
            fail("DispatchingException expected");
        } catch (final DispatchingException e) {
            assertEquals(Bar.class, e.getType());
        }
    }

    public void testHashCodeIsDifferentForEachProxy() throws Exception {
        Mock fooMock = mock(Foo.class);

        Object proxy1 = Dispatching.object(new Class[]{Foo.class}, new Object[]{fooMock.proxy()}, getFactory());
        Object proxy2 = Dispatching.object(new Class[]{Foo.class}, new Object[]{fooMock.proxy()}, getFactory());

        assertFalse(proxy1.hashCode() == proxy2.hashCode());
    }

    public void testStringRepresentationContainsImplementedTypes() throws Exception {
        Mock fooBarMock = mock(FooBar.class);

        Object foobar = Dispatching.object(
                new Class[]{Foo.class, Bar.class}, new Object[]{fooBarMock.proxy()}, getFactory());

        String string = foobar.toString();
        assertTrue(string.indexOf(Foo.class.getName()) >= 0);
        assertTrue(string.indexOf(Bar.class.getName()) >= 0);
    }

    public void testTwoProxiesAreEqualIfSameTypesAreDelegatedToEqualDelegates() throws Exception {
        Object proxy1 = Dispatching.object(new Class[]{Comparable.class, Runnable.class, List.class}, new Object[]{
                new ArrayList(), "Hello", Thread.currentThread()}, getFactory());
        Object proxy2 = Dispatching.object(new Class[]{List.class, Runnable.class, Comparable.class}, new Object[]{
                "Hello", new ArrayList(), Thread.currentThread()}, getFactory());

        assertEquals(proxy1, proxy2);
    }

    public void testTwoProxiesAreNotEqualIfSameTypesAreDelegatedToAtLeastOneNonEqualDelegate() throws Exception {
        Object proxy1 = Dispatching.object(new Class[]{Comparable.class, Runnable.class, List.class}, new Object[]{
                new ArrayList(), "Foo", Thread.currentThread()}, getFactory());
        Object proxy2 = Dispatching.object(new Class[]{List.class, Runnable.class, Comparable.class}, new Object[]{
                "Bar", new ArrayList(), Thread.currentThread()}, getFactory());

        assertFalse(proxy1.equals(proxy2));
    }

    private void useSerializedProxy(Resetter resetter) {
        assertTrue(resetter.reset(this));
    }

    public void testSerializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedProxy((Resetter)serializeWithJDK(Dispatching.object(
                new Class[]{Resetter.class}, new Object[]{new NoOperationResetter()}, getFactory())));
    }

    public void testSerializeWithXStream() throws IOException, ClassNotFoundException {
        useSerializedProxy((Resetter)serializeWithXStream(Dispatching.object(
                new Class[]{Resetter.class}, new Object[]{new NoOperationResetter()}, getFactory())));
    }

    public void testSerializeWithXStreamInPureReflectionMode() throws IOException, ClassNotFoundException {
        useSerializedProxy((Resetter)serializeWithXStreamAndPureReflection(Dispatching.object(
                new Class[]{Resetter.class}, new Object[]{new NoOperationResetter()}, getFactory())));
    }
}
