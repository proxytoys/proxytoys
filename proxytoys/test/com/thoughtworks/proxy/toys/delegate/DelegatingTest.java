/*
 * Created on 05-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.delegate;

import java.rmi.RemoteException;

import org.jmock.Mock;

import com.thoughtworks.proxy.ProxyTestCase;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class DelegatingTest extends ProxyTestCase {
    
    public interface Foo {
        String getSomething() throws RemoteException;
    }
    
    private Mock fooMock;
    private Foo foo;
    
    public void setUp() throws Exception {
        super.setUp();
        fooMock = new Mock(Foo.class);
        foo = createProxy(fooMock.proxy());
    }
    
    public static class SimpleImpl {
        public String getSomething() throws RemoteException {
            return "some thing";
        }
    }

    private Foo createProxy(Object impl) {
        return (Foo)Delegating.object(Foo.class, impl, getFactory());
    }

    public void testShouldDelegateMethodsCalledOnInterface() throws Exception {
        // expect
        fooMock.expects(once()).method("getSomething").withNoArguments()
            .will(returnValue("some thing"));
        
        // execute
        final String result = foo.getSomething();
        
        // verify
        assertEquals("some thing", result);
    }

    public void testShouldPropagateExceptionFromDelegate() throws Exception {
        // setup
        Exception cause = new UnsupportedOperationException("sorry");
        
        // expect
        fooMock.expects(once()).method("getSomething").withNoArguments()
            .will(throwException(cause));

        // execute
        try {
            foo.getSomething();
            fail("Mock should have thrown exception");
        } catch (UnsupportedOperationException e) {
            // verify
            assertSame(cause, e);
        }
    }

    public void testShouldThrowDelegationExceptionIfDelegateMethodDoesNotExist() throws Exception {
        // setup
        foo = createProxy(new Object());
        
        // execute
        try {
            foo.getSomething();
            fail("Should have thrown exception");
        } catch (DelegationException expected) {
        }
    }
    
    public void testShouldIgnoreCallsToNullDelegate() throws Exception {
    	foo = createProxy(null);
        assertNull(foo.getSomething());
    }
}
