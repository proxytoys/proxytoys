/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.echo;

import com.thoughtworks.proxy.NewProxyTestCase;
import static com.thoughtworks.proxy.toys.echo.Echoing.echoable;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class EchoingTest extends NewProxyTestCase {


    public interface Simple {
        void doSomething();
    }

    private Simple simpleMock;

    @Before
    public void setUp() throws Exception {
        simpleMock = mock(Simple.class);
    }

    @Test
    public void shouldEchoMethodNameAndArgs() throws Exception {
        // setup
        Writer out = new StringWriter();
        Simple foo = (Simple) echoable(Simple.class).withPrintWriter(new PrintWriter(out)).build(getFactory());

        // execute
        foo.doSomething();

        // verify
        assertContains("Simple.doSomething()", out);
    }

    @Test
    public void shouldDelegateCalls() throws Exception {
        // setup
        Writer out = new StringWriter();
        Simple foo = (Simple) echoable(Simple.class).withPrintWriter(new PrintWriter(out)).withDelegateObject(simpleMock).build(getFactory());


        // execute
        foo.doSomething();

        // verify
        verify(simpleMock).doSomething();
    }

    public interface Inner {
        String getName();
    }

    public interface Outer {
        Inner getInner();
    }

    @Test
    public void shouldRecursivelyReturnEchoProxiesForInterfaces() throws Exception {
        // setup
        Inner innerMock = mock(Inner.class);
        Outer outerMock = mock(Outer.class);
        StringWriter out = new StringWriter();

        Outer outer = (Outer) echoable(Outer.class).withDelegateObject(outerMock).withPrintWriter(new PrintWriter(out)).build(getFactory());

        // expect
        when(outerMock.getInner()).thenReturn(innerMock);
        when(innerMock.getName()).thenReturn("inner");
        // execute
        String result = outer.getInner().getName();

        // verify
        assertEquals("inner", result);
        assertContains("Outer.getInner()", out);
        assertContains("Inner.getName()", out);
        verify(outerMock).getInner();
        verify(innerMock).getName();
    }

    @Test
    public void shouldRecursivelyReturnEchoProxiesEvenForMissingImplementations() throws Exception {
        // setup
        StringWriter out = new StringWriter();
        Outer outer = (Outer) echoable(Outer.class).withPrintWriter(new PrintWriter(out)).build(getFactory());

        // execute
        outer.getInner().getName();

        // verify
        assertContains("Outer.getInner()", out);
        assertContains("Inner.getName()", out);
    }

    private static void assertContains(String expected, Object textObject) {
        String text = textObject.toString();
        assertTrue("Expected [" + expected + "] in text:\n[" + text + "]", text.indexOf(expected) != -1);
    }
}
