package com.thoughtworks.proxy;

import org.jmock.MockObjectTestCase;

/**
 * @author Dan North
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public abstract class ProxyTestCase extends MockObjectTestCase {
    /**
     * A publicly settable <tt>ProxyFactory</tt>.
     * 
     * <p>The value of this factory is captured by the constructor
     * of each test case, so the same test case can be used to test
     * multiple factories.</p>
     * 
     * @see com.thoughtworks.proxy.factory.CglibProxyFactory
     * @see com.thoughtworks.proxy.factory.StandardProxyFactory
     * @see AllTests#suite()
     */
    public static ProxyFactory FACTORY;
    
    protected final ProxyFactory proxyFactory;

	protected ProxyTestCase() {
		proxyFactory = createProxyFactory();
	}

    /**
     * Get a reference to a proxy factory.
     * 
     * Override this to force a particular factory.
     */
	protected ProxyFactory createProxyFactory() {
		return FACTORY;
	}
}