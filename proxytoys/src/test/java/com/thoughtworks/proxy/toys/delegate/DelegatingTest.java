/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 05-Feb-2004
 */
package com.thoughtworks.proxy.toys.delegate;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;


/**
 * @author Dan North
 * @author J&ouml;rg Schaible
 * @author Tianshuo Deng
 */
public class DelegatingTest extends AbstractProxyTest {

    public interface Foo {
        String getSomething() throws RemoteException;
    }

    public static class FinalizingImpl {
        private StringBuilder buffer;

        public FinalizingImpl(StringBuilder buffer) {
            this.buffer = buffer;
        }

        protected String getSomething() {
            return "another thing";
        }

        @Override
        protected void finalize() throws Throwable {
            buffer.append("finalized");
        }
    }

    private Foo fooMock;
    private Foo foo;

    @Before
    public void setUp() throws Exception {
        fooMock = mock(Foo.class);
        foo = createProxy(fooMock);
    }

    private Foo createProxy(Object impl) {

        return Delegating.proxy(Foo.class).with(impl).build(getFactory());
    }

    @Test
    public void shouldDelegateMethodsCalledOnInterface() throws Exception {
        // expect
        when(fooMock.getSomething()).thenReturn("some thing");

        // execute
        final String result = foo.getSomething();

        // verify
        assertEquals("some thing", result);
        verify(fooMock).getSomething();
    }

    @Test
    public void shouldPropagateExceptionFromDelegate() throws Exception {
        // setup
        Exception cause = new UnsupportedOperationException("sorry");

        // expect
        when(fooMock.getSomething()).thenThrow(cause);
        // execute
        try {
            foo.getSomething();
            fail("Mock should have thrown exception");
        } catch (UnsupportedOperationException e) {
            // verify
            assertSame(cause, e);
        }
        verify(fooMock).getSomething();


    }

    @Test
    public void shouldThrowDelegationExceptionIfDelegateMethodDoesNotExist() throws Exception {
        // setup
        Object impl = new Object();
        foo = createProxy(impl);

        // execute

        try {
            foo.getSomething();
            fail("Should have thrown exception");
        } catch (DelegationException e) {
            assertEquals(impl, e.getDelegate());
        }

    }

    @Test
    public void shouldIgnoreCallsToNullDelegate() throws Exception {
        foo = createProxy(null);
        assertNull(foo.getSomething());
    }

    @Test
    public void shouldCompareEqualToItself() {
        String string = "some thing";
        foo = createProxy(string);
        assertEquals(foo, foo);
    }

    @Test
    public void shouldCompareEqualToDelegatedInstance() {
        String string = "some thing";
        foo = createProxy(string);
        assertEquals(foo, string);
    }

    @Test
    public void shouldCompareEqualToAnotherDelegatingProxyOnTheSameInstance() {
        String string = "some thing";
        foo = createProxy(string);
        assertEquals(foo, createProxy(string));
    }

    @Test
    public void shouldCompareEqualToANestedProxyOnTheSameInstance() {
        String string = "some thing";
        foo = createProxy(string);
        assertEquals(foo, createProxy(foo));
    }

    @Test
    public void shouldHaveSameHashCodeAsItself() {
        String string = "some thing";
        foo = createProxy(string);
        assertEquals(foo.hashCode(), foo.hashCode());
    }

    @Test
    public void shouldHaveSameHashCodeAsDelegatedInstance() {
        String string = "some thing";
        foo = createProxy(string);
        assertEquals(foo.hashCode(), string.hashCode());
    }

    @Test
    public void shouldHaveSameHashCodeAsAnotherDelegatingProxyOnTheSameInstance() {
        String string = "some thing";
        foo = createProxy(string);
        assertEquals(foo.hashCode(), createProxy(string).hashCode());
    }

    @Test
    public void shouldHaveSameHashCodeAsANestedProxyOnTheSameInstance() {
        String string = "some thing";
        foo = createProxy(string);
        assertEquals(foo.hashCode(), createProxy(foo).hashCode());
    }

    public static interface Faculty {
        int calc(int i, Faculty fac);
    }

    @Test
    public void shouldSupportIndirectRecursion() {
        Faculty fac = new Faculty() {
            public int calc(int i, Faculty fac) {
                return i == 1 ? 1 : i * fac.calc(i - 1, fac);
            }
        };
        Faculty proxy = Delegating.proxy(Faculty.class).with(fac).build(getFactory());
        assertEquals(120, fac.calc(5, fac));
        assertEquals(120, proxy.calc(5, proxy));
    }

    @Test(expected = DelegationException.class)
    public void shouldNotBeAbleToCallProtectedMethods() throws Exception {
        FinalizingImpl finalizing = new FinalizingImpl(new StringBuilder());
        foo = createProxy(finalizing);

        foo.getSomething();
        fail("Protected method called");
    }

    @Test
    public void shouldHandleFinalizeOnProxyOnly() throws Exception {
        final StringBuilder buffer = new StringBuilder();
        FinalizingImpl finalizing = new FinalizingImpl(buffer);
        foo = createProxy(finalizing);
        foo = null;
        System.gc();
        System.gc();
        assertEquals("", buffer.toString());
    }

    @Test
    public void shouldHandleFinalizeOnProxyAndDelegate() throws Exception {
        final StringBuilder buffer = new StringBuilder();
        {
            foo = createProxy(new FinalizingImpl(buffer));
            foo = null;
        }
        System.gc();
        Thread.sleep(10);
        System.gc();
        System.runFinalization();
        assertEquals("finalized", buffer.toString());
    }

    static class CompatibleFoo {
        public String getSomething() {
            return "Foo";
        }
    }

    @Test
    public void testDefaultProxyIsSignatureCompatible() throws RemoteException {
        Foo foo = createProxy(new CompatibleFoo());
        assertEquals("Foo", foo.getSomething());
    }
}
