/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactoryTest;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtilsTest;
import com.thoughtworks.proxy.toys.pool.PoolTest;
import junit.framework.Test;
import junit.framework.TestSuite;


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
        suite.addTestSuite(CglibProxyFactoryTest.class);

        // Miscellaneous
        suite.addTestSuite(ReflectionUtilsTest.class);

        return suite;
    }

    /**
     * Create a suite based on a particular {@link ProxyFactory} by setting the static
     * {@link ProxyTestCase#PROXY_FACTORY} property.
     * <p>
     * The <tt>addTestSuite(Class)</tt> method instantiates the test class which picks up the current value of the
     * static factory. It relies on the fact that the test runner instantiates all the test classes up front when it
     * builds the <tt>Suite</tt>, and then runs them all in a second pass.
     * </p>
     */
    private static Test createProxyFactorySuite(ProxyFactory factory, String type) {
        TestSuite suite = new TestSuite("Tests using " + type + "ProxyFactory");
        ProxyTestCase.PROXY_FACTORY = factory;
        suite.addTestSuite(ProxyFactoryTest.class);
        return suite;
    }
}
