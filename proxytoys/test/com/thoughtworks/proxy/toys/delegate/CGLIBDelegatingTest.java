package com.thoughtworks.proxy.toys.delegate;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CGLIBProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CGLIBDelegatingTest extends DelegatingTestCase {
    protected ProxyFactory createProxyFactory() {
        return new CGLIBProxyFactory();
    }
}
