/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.decorate.DecoratingTest;
import com.thoughtworks.proxy.toys.delegate.DelegatingTest;
import com.thoughtworks.proxy.toys.echo.EchoingTest;
import com.thoughtworks.proxy.toys.failover.FailoverTest;
import com.thoughtworks.proxy.toys.hide.CglibHidingTest;
import com.thoughtworks.proxy.toys.hide.HidingTest;
import com.thoughtworks.proxy.toys.multicast.CglibMulticastTest;
import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospectorTest;
import com.thoughtworks.proxy.toys.multicast.MulticastTest;
import com.thoughtworks.proxy.toys.nullobject.CglibNullTest;
import com.thoughtworks.proxy.toys.nullobject.NullTest;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class AllTests {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.thoughtworks.proxy");
        
        // Tests based on ProxyTestCase with different factories
        suite.addTest(createProxyFactorySuite(new StandardProxyFactory(), "Standard"));
        suite.addTest(createProxyFactorySuite(new CglibProxyFactory(), "Cglib"));
        
        // CGLIB-specific tests
        suite.addTestSuite(CglibHidingTest.class);
        suite.addTestSuite(CglibMulticastTest.class);
        suite.addTestSuite(CglibNullTest.class);
        
        // Miscellaneous
        suite.addTestSuite(ClassHierarchyIntrospectorTest.class);
        
        return suite;
	}

    /**
     * Create a suite based on a particular {@link ProxyFactory} by setting
     * the static {@link ProxyTestCase#FACTORY} property.
     * 
     * <p>The <tt>addTestSuite(Class)</tt> method instantiates the test class
     * which picks up the current value of the static factory. It relies on
     * the fact that the test runner instantiates all the test classes up front
     * when it builds the <tt>Suite</tt>, and then runs them all in a second pass.</p>
     */
	private static Test createProxyFactorySuite(ProxyFactory factory, String type) {
		TestSuite suite = new TestSuite("Tests using " + type + "ProxyFactory");
		ProxyTestCase.FACTORY = factory;
		suite.addTestSuite(DecoratingTest.class);
		suite.addTestSuite(DelegatingTest.class);
		suite.addTestSuite(EchoingTest.class);
		suite.addTestSuite(FailoverTest.class);
		suite.addTestSuite(HidingTest.class);
		suite.addTestSuite(MulticastTest.class);
		suite.addTestSuite(NullTest.class);
		return suite;
	}
}
