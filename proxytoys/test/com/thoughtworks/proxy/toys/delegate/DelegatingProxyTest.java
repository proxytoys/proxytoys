/*
 * Created on 05-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.delegate;

import java.rmi.RemoteException;

import junit.framework.TestCase;

import com.thoughtworks.proxy.toys.delegate.DelegatingProxy;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class DelegatingProxyTest extends TestCase {
    
    public interface Foo {
        String getSomething() throws RemoteException;
    }

    public static class SimpleImpl {
        public String getSomething() throws RemoteException {
            return "some thing";
        }
    }

    private Foo createProxy(Object impl) {
        return (Foo)DelegatingProxy.newProxyInstance(Foo.class, impl);
    }

    public void testShouldDelegateMethodsCalledOnInterface() throws Exception {
        // setup
        Foo remote = createProxy(new SimpleImpl());

        // execute, verify
        final String result = remote.getSomething();
        assertEquals("some thing", result);
    }

    public static class ExceptionThrowingImpl {
        public String getSomething() {
            throw new UnsupportedOperationException("sorry");
        }
    }
    
    public void testShouldPropagateExceptionFromDelegate() throws Exception {
        try {
            Foo remote = createProxy(new ExceptionThrowingImpl());
            remote.getSomething();
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
            assertEquals("sorry", e.getMessage());
        }
    }

    public static class MethodMissingImpl {
    }

    public void testShouldThrowRemoteExceptionIfDelegateMethodDoesNotExist()
    throws Exception {
        try {
            Foo remote = createProxy(new MethodMissingImpl());
            remote.getSomething();
            fail();
        } catch (RemoteException e) {
            // expected
        }
    }
    
    public void testShouldIgnoreCallsToNullDelegate() throws Exception {
    	Foo remote = createProxy(null);
        assertNull(remote.getSomething());
    }
}
