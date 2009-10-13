/*
 * Created on 05-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.delegate;

import com.thoughtworks.proxy.AbstractProxyTest;
import static com.thoughtworks.proxy.toys.delegate.Delegating.delegatable;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author J&ouml;rg Schaible
 */
public class DelegatingTest extends AbstractProxyTest {

    public interface Foo {
        String getSomething() throws RemoteException;
    }

    public static class FinalizingImpl {
        private StringBuffer buffer;

        public FinalizingImpl(StringBuffer buffer) {
            this.buffer = buffer;
        }

        protected String getSomething() throws RemoteException {
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

        return delegatable(Foo.class).with(impl).build(getFactory());
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
        Faculty proxy = delegatable(Faculty.class).with(fac).build(getFactory());
        assertEquals(120, fac.calc(5, fac));
        assertEquals(120, proxy.calc(5, proxy));
    }

    @Test(expected = DelegationException.class)
    public void shouldNotBeAbleToCallProtectedMethods() throws Exception {
        FinalizingImpl finalizing = new FinalizingImpl(new StringBuffer());
        foo = createProxy(finalizing);

        foo.getSomething();
        fail("Protected method called");

    }

    @Test
    public void shouldHandleFinalizeOnProxyOnly() throws Exception {
        final StringBuffer buffer = new StringBuffer();
        FinalizingImpl finalizing = new FinalizingImpl(buffer);
        foo = createProxy(finalizing);
        foo = null;
        System.gc();
        System.gc();
        assertEquals("", buffer.toString());
    }

    @Test
    public void shouldHandleFinalizeOnProxyAndDelegate() throws Exception {
        final StringBuffer buffer = new StringBuffer();
        {
            foo = createProxy(new FinalizingImpl(buffer));
            foo = null;
        }
        System.gc();
        Thread.sleep(10);
        System.gc();
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
