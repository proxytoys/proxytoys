package com.thoughtworks.proxy;

import org.jmock.MockObjectTestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public abstract class ProxyTestCase extends MockObjectTestCase {
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