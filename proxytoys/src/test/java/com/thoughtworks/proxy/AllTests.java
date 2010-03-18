/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
import com.thoughtworks.proxy.toys.future.FutureTest;
import com.thoughtworks.proxy.toys.hotswap.CglibHotSwappingTest;
import com.thoughtworks.proxy.toys.hotswap.HotSwappingTest;
import com.thoughtworks.proxy.toys.multicast.CglibMulticastTest;
import com.thoughtworks.proxy.toys.multicast.MulticastTest;
import com.thoughtworks.proxy.toys.nullobject.CglibNullTest;
import com.thoughtworks.proxy.toys.nullobject.NullTest;
import com.thoughtworks.proxy.toys.pool.PoolTest;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AllTests.CglibSuite.class,
        AllTests.StandardSuite.class,
        CglibProxyFactoryTest.class,
        CglibEchoingTest.class,
        CglibHotSwappingTest.class,
        CglibMulticastTest.class,
        CglibNullTest.class,
        ReflectionUtilsTest.class
})
public class AllTests {
    public AllTests() {
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses({
            ProxyFactoryTest.class,
            DecoratingTest.class,
            DelegatingTest.class,
            DispatchingTest.class,
            EchoingTest.class,
            FailoverTest.class,
            FutureTest.class,
            HotSwappingTest.class,
            MulticastTest.class,
            NullTest.class,
            PoolTest.class
    })
    static class CglibSuite {
        public CglibSuite() {
            AbstractProxyTest.PROXY_FACTORY = new CglibProxyFactory();
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
            FutureTest.class,
            HotSwappingTest.class,
            MulticastTest.class,
            NullTest.class,
            PoolTest.class
    })
    static class StandardSuite {
        public StandardSuite() {
            AbstractProxyTest.PROXY_FACTORY = new StandardProxyFactory();
        }
    }
}
