/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxytoys.decorate;

import java.lang.reflect.Method;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class DecoratingProxyTest extends MockObjectTestCase {
    private static final String decorateException = "decorateException";
	private static final String decorateResult = "decorateResult";
	private static final String beforeMethodStarts = "beforeMethodStarts";
	private static final String doSomething = "doSomething";
    private static final Method doSomethingMethod;
    
    static {
        try {
			doSomethingMethod = Foo.class.getMethod(doSomething, null);
		} catch (Exception e) {
			throw new NoSuchMethodError("Foo.doSomething()");
		}
    }
    
    private Mock fooMock;
    private Mock decoratorMock;
    private Foo foo;
    
    public interface Foo {
        String doSomething();
    }
    
    public void setUp() throws Exception {
        fooMock = new Mock(Foo.class);
        fooMock.stubs();
        decoratorMock = new Mock(InvocationDecorator.class);
        decoratorMock.stubs();
    }
    
    // TODO fixme!
    public void TODO_testShouldInterceptMethodInvocation() throws Exception {
		// expect
        decoratorMock.expects(once()).
            method(beforeMethodStarts).with(eq(foo), eq(doSomethingMethod), ANYTHING);
        
        fooMock.expects(once())
            .method(doSomething).withNoArguments()
            .after(decoratorMock, beforeMethodStarts)
            .will(returnValue("hello"));
        
		// execute
        foo = (Foo) DecoratingProxy.newProxyInstance(Foo.class, fooMock.proxy(),
                (InvocationDecorator)decoratorMock.proxy());
        foo.doSomething();
	}
    
    public void _testShouldInterceptMethodSuccess() throws Exception {
		// expect
        fooMock.expects(once())
            .method(doSomething).withNoArguments();
    
        decoratorMock.expects(once())
            .method(decorateResult).with(eq(null)).after(fooMock, doSomething);
    
        // execute
        foo = (Foo) DecoratingProxy.newProxyInstance(Foo.class, fooMock.proxy(),
                (InvocationDecorator)decoratorMock.proxy());
        foo.doSomething();
	}
    
    public void testShouldInterceptMethodFailure() throws Exception {
		// setup
        RuntimeException exception = new RuntimeException();
		
		// expect
		fooMock.expects(once())
            .method(doSomething).withNoArguments().will(throwException(exception));
    
        decoratorMock.expects(once())
            .method(decorateException).with(eq(exception));
    
        // execute
        foo = (Foo) DecoratingProxy.newProxyInstance(Foo.class, fooMock.proxy(),
                (InvocationDecorator)decoratorMock.proxy());
        try {
			foo.doSomething();
            fail("Mock should have thrown exception");
		} catch (RuntimeException expected) {
		}
	}
}
