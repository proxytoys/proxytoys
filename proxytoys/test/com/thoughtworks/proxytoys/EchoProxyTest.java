 /*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxytoys;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class EchoProxyTest extends TestCase {
    
    private interface Foo {
        void doSomething();
    }
    
    public void testShouldEchoMethodNameAndArgs() throws Exception {
        // setup
        Writer out = new StringWriter();
    	Foo foo = (Foo) EchoProxy.newProxyInstance(Foo.class, new PrintWriter(out));
        
        // execute
        foo.doSomething();
        
        // verify
        assertContains("Foo.doSomething()", out);
    }
    
    public static class FooImpl implements Foo {
        public boolean wasCalled = false;
        
        public void doSomething() {
            wasCalled = true;
        }
    }
    
    public void testShouldDelegateCalls() throws Exception {
        // setup
        Writer out = new StringWriter();
        FooImpl impl = new FooImpl();
        Foo foo = (Foo) EchoProxy.newProxyInstance(Foo.class, impl, new PrintWriter(out));
        
        // execute
        foo.doSomething();
        
        // verify
        assertTrue(impl.wasCalled);
    }
    
    public interface Inner {
        String getName();
    }
    
    public interface Outer {
        Inner getInner();
    }
    
    public static class InnerImpl implements Inner {
        public String getName() {
            return "inner";
        }
    }
    
    public static class OuterImpl implements Outer {
        public Inner getInner() {
            return new InnerImpl();
        }
    }
    
    public void testShouldRecursivelyReturnEchoProxiesForInterfaces() throws Exception {
        // setup
        StringWriter out = new StringWriter();
    	Outer outer = (Outer)EchoProxy.newProxyInstance(
    	        Outer.class,
    	        new OuterImpl(),
    	        new PrintWriter(out));
        
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
        Outer outer = (Outer)EchoProxy.newProxyInstance(
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
