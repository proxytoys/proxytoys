/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 24.02.2005 by Jörg Schaible
 */
package com.thoughtworks.proxy.toys.dispatch;

import com.thoughtworks.proxy.ProxyTestCase;

import org.jmock.Mock;

public class DispatchingTest extends ProxyTestCase {
    
    public interface Foo {
        String getSomething();
    }
    
    public interface Bar {
        void doSomething(String s);
    }
    
    public interface FooMimic {
        String getSomething();
    }
    
    public interface BarSimilar {
        void doSomething(int i);
    }

    public void testCallsAreDispatchedBetweenObjects() throws Exception {
        Mock fooMock = mock(Foo.class);
        Mock barMock = mock(Bar.class);
        
        Object foobar = Dispatching.object(new Class[]{Foo.class, Bar.class}, new Object[]{fooMock.proxy(), barMock.proxy()}, getFactory());

        fooMock.expects(once()).method("getSomething").withNoArguments().will(returnValue("some thing"));
        barMock.expects(once()).method("doSomething").with(eq("some thing"));
        
        assertEquals("some thing", ((Foo) foobar).getSomething());
        ((Bar) foobar).doSomething("some thing");
    }

    // This is a proxy limitation ... unfortunately
    public void testOnlyFirstOfMethodsWithSameSignaturesIsCalled() throws Exception {
        Mock fooMock = mock(Foo.class);
        Mock fooMimicMock = mock(FooMimic.class);
        
        Object foo = Dispatching.object(new Class[]{Foo.class, FooMimic.class}, new Object[]{fooMock.proxy(), fooMimicMock.proxy()}, getFactory());

        fooMock.expects(once()).method("getSomething").withNoArguments().will(returnValue("some thing"));
        fooMock.expects(once()).method("getSomething").withNoArguments().will(returnValue("some thing"));
        
        assertEquals("some thing", ((Foo) foo).getSomething());
        assertEquals("some thing", ((FooMimic) foo).getSomething());
    }

    public void testMethodsWithSameNameButDifferentSignatureAreDistinct() throws Exception {
        Mock barMock = mock(Bar.class);
        Mock barSimilarMock = mock(BarSimilar.class);
        
        Object bar = Dispatching.object(new Class[]{Bar.class, BarSimilar.class}, new Object[]{barMock.proxy(), barSimilarMock.proxy()}, getFactory());

        barMock.expects(once()).method("doSomething").with(eq("some thing"));
        barSimilarMock.expects(once()).method("doSomething").with(eq(1));
        
        ((Bar) bar).doSomething("some thing");
        ((BarSimilar) bar).doSomething(1);
    }
}
