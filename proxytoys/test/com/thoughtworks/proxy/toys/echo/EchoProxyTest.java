/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.echo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.jmock.Mock;
import org.jmock.core.mixin.Invoked;
import org.jmock.core.mixin.Return;

import com.thoughtworks.proxy.toys.echo.Echoing;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class EchoProxyTest extends TestCase {
    
    private static final String getInner = "getInner";
	public interface Simple {
        void doSomething();
    }
    
    private Mock simpleMock;
    private Simple simpleImpl;
    
    public void setUp() throws Exception {
        simpleMock = new Mock(Simple.class);
        simpleImpl = (Simple) simpleMock.proxy();
    }
    
    public void testShouldEchoMethodNameAndArgs() throws Exception {
        // setup
        Writer out = new StringWriter();
    	Simple foo = (Simple) Echoing.object(Simple.class, new PrintWriter(out));
        
        // execute
        foo.doSomething();
        
        // verify
        assertContains("Simple.doSomething()", out);
    }
    
    public void testShouldDelegateCalls() throws Exception {
        // setup
        Writer out = new StringWriter();
        Simple simple = (Simple) Echoing.object(Simple.class, simpleImpl, new PrintWriter(out));
        
        // expect
        simpleMock.expects(Invoked.once()).method("doSomething");
        
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
        
    	Outer outer = (Outer)Echoing.object(
    	        Outer.class,
    	        outerMock.proxy(),
    	        new PrintWriter(out));
        
        // expect
        outerMock.expects(Invoked.once())
            .method(getInner).withNoArguments()
            .will(Return.value(innerMock.proxy()));
        
        innerMock.expects(Invoked.once())
            .method("getName").withNoArguments()
            .will(Return.value("inner"));
        
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
        Outer outer = (Outer)Echoing.object(
                Outer.class, null, new PrintWriter(out));
        
        // execute
        outer.getInner().getName();
        
        // verify
        assertContains("Outer.getInner()", out);
        assertContains("Inner.getName()", out);
    }

    private static void assertContains(String expected, Object textObject) {
        String text = textObject.toString();
        assertTrue("Expected [" + expected + "] in text:\n[" + text + "]",
                text.indexOf(expected) != -1);
    }
}
