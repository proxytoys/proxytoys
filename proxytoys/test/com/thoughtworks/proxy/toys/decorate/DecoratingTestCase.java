/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import java.lang.reflect.Method;

import org.jmock.Mock;

import com.thoughtworks.proxy.ProxyTestCase;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public abstract class DecoratingTestCase extends ProxyTestCase {
    private static final String decorateException = "decorateException";
	private static final String decorateResult = "decorateResult";
	private static final String beforeMethodStarts = "beforeMethodStarts";
	private static final String getSomething = "getSomething";
    private static final Method getSomethingMethod;
    
    static {
        try {
			getSomethingMethod = Foo.class.getMethod(getSomething, null);
		} catch (Exception e) {
			throw new NoSuchMethodError("Foo.doSomething()");
		}
    }
    
    private Mock fooMock;
    private Mock decoratorMock;
    private Foo foo;
    
    public interface Foo {
        String getSomething();
    }
    
    public void setUp() throws Exception {
        fooMock = new Mock(Foo.class);
        fooMock.stubs();
        decoratorMock = new Mock(InvocationDecorator.class);
        decoratorMock.stubs();
        foo = (Foo) Decorating.object(Foo.class, fooMock.proxy(),
                (InvocationDecorator)decoratorMock.proxy());
    }
    
    public void testShouldInterceptMethodInvocation() throws Exception {
		// expect
        decoratorMock.expects(once())
			.method(beforeMethodStarts).with(same(foo), eq(getSomethingMethod), NULL);
        
        fooMock.expects(once())
            .method(getSomething).withNoArguments()
            .after(decoratorMock, beforeMethodStarts)
            .will(returnValue("hello"));
        
		// execute
        foo.getSomething();
	}
    
    public void testShouldInterceptMethodSuccess() throws Exception {
		// expect
        fooMock.expects(once())
            .method(getSomething).withNoArguments()
			.will(returnValue("hello"));
    
        decoratorMock.expects(once())
            .method(decorateResult).with(eq("hello")).after(fooMock, getSomething)
			.will(returnValue("world"));
    
        // execute
        String result = foo.getSomething();
        
        // verify
        assertEquals("world", result);
	}
    
    public static class MyException extends RuntimeException {}
    
    public void testShouldInterceptMethodFailure() throws Exception {
        
        MyException exception = new MyException();
        MyException decoratedException = new MyException();
		
		// expect
		fooMock.expects(once())
            .method(getSomething).withNoArguments()
			.will(throwException(exception));
    
        decoratorMock.expects(once())
            .method(decorateException).with(same(exception))
			.will(returnValue(decoratedException));
    
        // execute
        try {
			foo.getSomething();
            fail("Mock should have thrown exception");
		} catch (MyException oops) {
			assertSame(decoratedException, oops);
		}
	}
}
