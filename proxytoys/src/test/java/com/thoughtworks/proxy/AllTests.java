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
import com.thoughtworks.proxy.toys.decorate.DecoratingTest;
import com.thoughtworks.proxy.toys.delegate.DelegatingTest;
import com.thoughtworks.proxy.toys.dispatch.DispatchingTest;
import com.thoughtworks.proxy.toys.echo.CglibEchoingTest;
import com.thoughtworks.proxy.toys.echo.EchoingTest;
import com.thoughtworks.proxy.toys.failover.FailoverTest;
import com.thoughtworks.proxy.toys.hotswap.CglibHotSwappingTest;
import com.thoughtworks.proxy.toys.hotswap.HotSwappingTest;
import com.thoughtworks.proxy.toys.multicast.CglibMulticastTest;
import com.thoughtworks.proxy.toys.multicast.MulticastTest;
import com.thoughtworks.proxy.toys.nullobject.CglibNullTest;
import com.thoughtworks.proxy.toys.nullobject.NullTest;
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
        suite.addTestSuite(CglibEchoingTest.class);
        suite.addTestSuite(CglibHotSwappingTest.class);
        suite.addTestSuite(CglibMulticastTest.class);
        suite.addTestSuite(CglibNullTest.class);

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
        suite.addTestSuite(DispatchingTest.class);
        suite.addTestSuite(EchoingTest.class);
        suite.addTestSuite(FailoverTest.class);
        suite.addTestSuite(HotSwappingTest.class);
        suite.addTestSuite(MulticastTest.class);
        suite.addTestSuite(NullTest.class);
        suite.addTestSuite(PoolTest.class);
        return suite;
    }
}
