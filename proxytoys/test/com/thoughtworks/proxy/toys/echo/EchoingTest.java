/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.echo;

import com.thoughtworks.proxy.ProxyTestCase;

import org.jmock.Mock;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class EchoingTest extends ProxyTestCase {

    private static final String getInner = "getInner";

    public interface Simple {
        void doSomething();
    }

    private Mock simpleMock;
    private Simple simpleImpl;

    public void setUp() throws Exception {
        simpleMock = mock(Simple.class);
        simpleImpl = (Simple)simpleMock.proxy();
    }

    public void testShouldEchoMethodNameAndArgs() throws Exception {
        // setup
        Writer out = new StringWriter();
        Simple foo = (Simple)Echoing.object(Simple.class, null, new PrintWriter(out), getFactory());

        // execute
        foo.doSomething();

        // verify
        assertContains("Simple.doSomething()", out);
    }

    public void testShouldDelegateCalls() throws Exception {
        // setup
        Writer out = new StringWriter();
        Simple simple = (Simple)Echoing.object(Simple.class, simpleImpl, new PrintWriter(out), getFactory());

        // expect
        simpleMock.expects(once()).method("doSomething");

        // execute
        simple.doSomething();

        // verify
        simpleMock.verify();
    }

    public interface Inner {
        String getName();
    }

    public interface Outer {
        Inner getInner();
    }

    public void testShouldRecursivelyReturnEchoProxiesForInterfaces() throws Exception {
        // setup
        Mock innerMock = new Mock(Inner.class);
        Mock outerMock = new Mock(Outer.class);
        StringWriter out = new StringWriter();

        Outer outer = (Outer)Echoing.object(Outer.class, outerMock.proxy(), new PrintWriter(out), getFactory());

        // expect
        outerMock.expects(once()).method(getInner).withNoArguments().will(returnValue(innerMock.proxy()));

        innerMock.expects(once()).method("getName").withNoArguments().will(returnValue("inner"));

        // execute
        String result = outer.getInner().getName();

        // verify
        assertEquals("inner", result);
        assertContains("Outer.getInner()", out);
        assertContains("Inner.getName()", out);
    }

    public void testShouldRecursivelyReturnEchoProxiesEvenForMissingImplementations() throws Exception {
        // setup
        StringWriter out = new StringWriter();
        Outer outer = (Outer)Echoing.object(Outer.class, null, new PrintWriter(out), getFactory());

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
