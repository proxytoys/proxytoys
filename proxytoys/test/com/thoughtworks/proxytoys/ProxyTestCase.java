package com.thoughtworks.proxytoys;

import junit.framework.TestCase;
import com.thoughtworks.proxytoys.ProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public abstract class ProxyTestCase extends TestCase {
    protected ProxyFactory proxyFactory;

    public ProxyTestCase() {
        proxyFactory = createProxyFactory();
    }

    protected abstract ProxyFactory createProxyFactory();
}