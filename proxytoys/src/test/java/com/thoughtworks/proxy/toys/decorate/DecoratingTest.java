/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 03-May-2004
 */
package com.thoughtworks.proxy.toys.decorate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;
import com.thoughtworks.proxy.mockito.SameArrayMatcher;

/**
 * @author Dan North
 * @author Tianshuo Deng
 */
public class DecoratingTest extends AbstractProxyTest {
    private static final String getSomething = "getSomething";

    private static final Method getSomethingMethod;

    static {
        try {
            getSomethingMethod = Foo.class.getMethod(getSomething, String.class);
        } catch (Exception e) {
            throw new NoSuchMethodError("Foo.getSomething(String)");
        }
    }

    private Foo foo;
    private Decorator<Foo> decoratorMock;
    private Foo fooMock;

    public interface Foo {
        String getSomething(String arg);
    }


	@Before
    public void setUp() throws Exception {
        fooMock = mock(Foo.class);
        @SuppressWarnings("unchecked")
        Decorator<Foo> mock = mock(Decorator.class);
		decoratorMock = mock;
        assertNotNull(fooMock);
        foo = Decorating.proxy(Foo.class).with(fooMock).visiting(decoratorMock).build();
        assertNotNull(foo);
    }

    private Object[] toArray(Object value) {
        return new Object[]{value};
    }


    @Test
    public void shouldInterceptMethodInvocation() throws Exception {
        // expect
        when(decoratorMock.beforeMethodStarts(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("fooMock"))))).thenReturn(toArray("decorated"));
        when(fooMock.getSomething(eq("decorated"))).thenReturn("hello");

        // execute
        foo.getSomething("fooMock");

        // verify
        verify(decoratorMock).beforeMethodStarts(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("fooMock"))));
        verify(fooMock).getSomething(eq("decorated"));
    }

    @Test
    public void shouldInterceptMethodSuccess() throws Exception {
        // expect
        when(decoratorMock.beforeMethodStarts(same(foo), any(Method.class), any(Object[].class))).thenReturn(toArray("ignored"));
        when(fooMock.getSomething(any(String.class))).thenReturn("hello");
        when(decoratorMock.decorateResult(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("ignored"))), eq("hello"))).thenReturn("world");

        // execute
        String result = foo.getSomething("before");

        // verify
        assertEquals("world", result);
        verify(decoratorMock).beforeMethodStarts(same(foo), any(Method.class), any(Object[].class));
        verify(fooMock).getSomething(any(String.class));
        verify(decoratorMock).decorateResult(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("ignored"))), eq("hello"));

    }

    @SuppressWarnings("serial")
    public static class MyException extends RuntimeException {
    }

    @Test
    public void shouldInterceptTargetException() throws Exception {
        MyException exception = new MyException();
        MyException decoratedException = new MyException();

        // expect
        when(decoratorMock.beforeMethodStarts(same(foo), any(Method.class), any(Object[].class))).thenReturn(toArray("ignored"));
        when(fooMock.getSomething(anyString())).thenThrow(exception);
        when(decoratorMock.decorateTargetException(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("ignored"))), same(exception))).thenReturn(decoratedException);

        // execute
        try {
            foo.getSomething("value");
            fail("Mock should have thrown exception");
        } catch (MyException oops) {
            assertSame(decoratedException, oops);
        }

        // verify
        verify(decoratorMock).beforeMethodStarts(same(foo), any(Method.class), any(Object[].class));
        verify(fooMock).getSomething(anyString());
        verify(decoratorMock).decorateTargetException(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("ignored"))), same(exception));

    }

    public class MethodMissingImpl {
    }

    @Test(expected = MyException.class)
    public void shouldInterceptInvocationException() throws Exception {

        // setup
        final Throwable[] thrown = new Throwable[1]; // hack for inner class
        final MyException decoratedException = new MyException();

        foo = Decorating.proxy(new MethodMissingImpl(), Foo.class).visiting(new Decorator<Foo>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Exception decorateInvocationException(Foo proxy, Method method, Object[] args, Exception cause) {
                thrown[0] = cause;
                return decoratedException;
            }
        }).build();

        // execute
		foo.getSomething("value");
		fail("Mock should have thrown exception");
    }

    static class AssertingDecorator extends Decorator<CharSequence> {
        private static final long serialVersionUID = 1L;

        @Override
        public Object[] beforeMethodStarts(CharSequence proxy, Method method, Object[] args) {
            assertNull(args);
            return super.beforeMethodStarts(proxy, method, args);
        }

    }

    private void useSerializedProxy(CharSequence sequence) {
        assertEquals("Test", sequence.toString());
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedProxy(serializeWithJDK(
        	Decorating.proxy(CharSequence.class).with("Test").visiting(new AssertingDecorator()).build(getFactory())));
    }

    @Test
    public void serializeWithXStream() {
        useSerializedProxy(serializeWithXStream(
        	Decorating.proxy(CharSequence.class).with("Test").visiting(new AssertingDecorator()).build(getFactory())));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        useSerializedProxy(serializeWithXStreamAndPureReflection(
        	Decorating.proxy(CharSequence.class).with("Test").visiting(new AssertingDecorator()).build(getFactory())));
    }
}
