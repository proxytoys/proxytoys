package com.thoughtworks.proxy.toys.failover;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CGLIBProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CGLIBFailoverTest extends FailoverTest {
    protected ProxyFactory createProxyFactory() {
        return new CGLIBProxyFactory();
    }
}