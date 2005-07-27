package com.thoughtworks.proxy;

import com.thoughtworks.proxy.factory.StandardProxyFactory;

import org.jmock.MockObjectTestCase;


/**
 * @author Dan North
 * @author Aslak Helles&oslash;y
 */
public abstract class ProxyTestCase extends MockObjectTestCase {
    /**
     * A publicly settable <tt>ProxyFactory</tt>.
     * <p>
     * The value of this factory is captured by the constructor of each test case, so the class can have a default constructor.
     * </p>
     * <p>
     * Note: by the time the tests run this will have changed, which is why there is an instance variable too.
     * </p>
     * 
     * @see com.thoughtworks.proxy.factory.CglibProxyFactory
     * @see com.thoughtworks.proxy.factory.StandardProxyFactory
     * @see AllTests#suite()
     */
    public static ProxyFactory PROXY_FACTORY = new StandardProxyFactory();

    /** the actual factory the tests will run against */
    private final ProxyFactory proxyFactory;

    protected ProxyTestCase() {
        proxyFactory = createProxyFactory();
    }

    /**
     * Get a reference to a proxy factory. Override this to force a particular factory.
     */
    protected ProxyFactory createProxyFactory() {
        return PROXY_FACTORY;
    }

    public ProxyFactory getFactory() {
        return proxyFactory;
    }
}