/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.ProxyTestCase;
import org.jmock.Mock;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class DecoratingTest extends ProxyTestCase {
    private static final String decorateException = "decorateException";
	private static final String decorateResult = "decorateResult";
	private static final String beforeMethodStarts = "beforeMethodStarts";
	private static final String getSomething = "getSomething";
    private static final Method getSomethingMethod;
    
    static {
        try {
			getSomethingMethod = Foo.class.getMethod(getSomething, new Class[] {String.class});
		} catch (Exception e) {
			throw new NoSuchMethodError("Foo.getSomething()");
		}
    }
    
    private Mock fooMock;
    private Mock decoratorMock;
    private Foo foo;
    
    public interface Foo {
        String getSomething(String arg);
    }
    
    public void setUp() throws Exception {
        fooMock = new Mock(Foo.class);
        decoratorMock = new Mock(InvocationDecorator.class);
        decoratorMock.stubs();
        assertNotNull(fooMock.proxy());
        foo = (Foo) Decorating.object(Foo.class, fooMock.proxy(),
                (InvocationDecorator)decoratorMock.proxy());
    }

    private Object[] toArray(Object value) {
        return new Object[] {value};
    }
    
    public void testShouldInterceptMethodInvocation() throws Exception {
		// expect
        decoratorMock.expects(once())
			.method(beforeMethodStarts)
            .with(same(foo), eq(getSomethingMethod), eq(toArray("foo")))
            .will(returnValue(toArray("decorated")));
        
        fooMock.expects(once())
            .method(getSomething).with(eq("decorated"))
            .after(decoratorMock, beforeMethodStarts)
            .will(returnValue("hello"));
        
		// execute
        foo.getSomething("foo");
	}
    
	public void testShouldInterceptMethodSuccess() throws Exception {
		// expect
        decoratorMock.expects(once())
            .method(beforeMethodStarts).withAnyArguments()
            .will(returnValue(toArray("ignored")));

        fooMock.expects(once())
            .method(getSomething).withAnyArguments()
			.will(returnValue("hello"));
    
        decoratorMock.expects(once())
            .method(decorateResult).with(eq("hello"))
            .after(fooMock, getSomething)
			.will(returnValue("world"));
    
        // execute
        String result = foo.getSomething("before");
        
        // verify
        assertEquals("world", result);
	}
    
    public static class MyException extends RuntimeException {}
    
    public void testShouldInterceptMethodFailure() throws Exception {
        decoratorMock.expects(once()).method(beforeMethodStarts)
            .will(returnValue(toArray("ignored")));
        
        MyException exception = new MyException();
        MyException decoratedException = new MyException();
		
		// expect
		fooMock.expects(once())
            .method(getSomething)
			.will(throwException(exception));
    
        decoratorMock.expects(once())
            .method(decorateException).with(same(exception))
			.will(returnValue(decoratedException));
    
        // execute
        try {
			foo.getSomething("value");
            fail("Mock should have thrown exception");
		} catch (MyException oops) {
			assertSame(decoratedException, oops);
		}
	}
}
