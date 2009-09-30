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
import com.thoughtworks.proxy.toys.echo.EchoingTest;
import com.thoughtworks.proxy.toys.echo.CglibEchoingTest;
import com.thoughtworks.proxy.toys.failover.FailoverTest;
import com.thoughtworks.proxy.toys.hotswap.HotSwappingTest;
import com.thoughtworks.proxy.toys.hotswap.CglibHotSwappingTest;
import com.thoughtworks.proxy.toys.multicast.MulticastTest;
import com.thoughtworks.proxy.toys.multicast.CglibMulticastTest;
import com.thoughtworks.proxy.toys.nullobject.NullTest;
import com.thoughtworks.proxy.toys.nullobject.CglibNullTest;
import com.thoughtworks.proxy.toys.pool.PoolTest;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CglibSuite.class,
        StandardSuite.class,
         CglibProxyFactoryTest.class,
         CglibEchoingTest.class,
         CglibHotSwappingTest.class,
         CglibMulticastTest.class,
         CglibNullTest.class,
        ReflectionUtilsTest.class
})
public class AllTests {
    public AllTests(){

    }
//    public static void main(String[] args) {
//        junit.textui.TestRunner.run(AllTests.suite());
//    }
//
//    public static Test suite() {
//        TestSuite suite = new TestSuite("Test for com.thoughtworks.proxy");
//
//        // Tests based on ProxyTestCase with different factories
//        suite.addTest(createProxyFactorySuite(new StandardProxyFactory(), "Standard"));
//        suite.addTest(createProxyFactorySuite(new CglibProxyFactory(), "Cglib"));
//
//        // CGLIB-specific tests
//
//        // Miscellaneous
//        suite.addTestSuite(ReflectionUtilsTest.class);
//
//        return suite;
//    }
//
//    /**
//     * Create a suite based on a particular {@link ProxyFactory} by setting the static
//     * {@link ProxyTestCase#PROXY_FACTORY} property.
//     * <p>
//     * The <tt>addTestSuite(Class)</tt> method instantiates the test class which picks up the current value of the
//     * static factory. It relies on the fact that the test runner instantiates all the test classes up front when it
//     * builds the <tt>Suite</tt>, and then runs them all in a second pass.
//     * </p>
//     */
//    private static Test createProxyFactorySuite(ProxyFactory factory, String type) {
//        TestSuite suite = new TestSuite("Tests using " + type + "ProxyFactory");
//        NewProxyTestCase.PROXY_FACTORY = factory;
//        return suite;
//    }
}

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ProxyFactoryTest.class,
        DecoratingTest.class,
        DelegatingTest.class,
        DispatchingTest.class,
        EchoingTest.class,
        FailoverTest.class,
        HotSwappingTest.class,
        MulticastTest.class,
        NullTest.class,
        PoolTest.class
})
class CglibSuite {
    public CglibSuite() {
          NewProxyTestCase.PROXY_FACTORY=new CglibProxyFactory();

    }
}




@RunWith(Suite.class)
@Suite.SuiteClasses({
        ProxyFactoryTest.class,
        DecoratingTest.class,
        DelegatingTest.class,
        DispatchingTest.class,
        EchoingTest.class,
        FailoverTest.class,
        HotSwappingTest.class,
        MulticastTest.class,
        NullTest.class,
        PoolTest.class
})
class StandardSuite {
    public StandardSuite() {
          NewProxyTestCase.PROXY_FACTORY=new StandardProxyFactory();
        
    }
}