package com.thoughtworks.proxy;

import org.jmock.MockObjectTestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public abstract class ProxyTestCase extends MockObjectTestCase {
    public static ProxyFactory FACTORY;
    private ProxyFactory proxyFactory;

	protected ProxyTestCase() {
		proxyFactory = createProxyFactory();
	}

	protected ProxyFactory createProxyFactory() {
		System.out.println(className(this) + ": " + className(FACTORY));
		return FACTORY;
	}

    private String className(Object o) {
        String name = o.getClass().getName();
        return name.substring(name.lastIndexOf('.') + 1);
	}

	public ProxyFactory proxyFactory() {
		return proxyFactory;
	}
}