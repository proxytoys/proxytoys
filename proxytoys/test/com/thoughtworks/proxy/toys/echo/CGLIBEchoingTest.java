package com.thoughtworks.proxy.toys.echo;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CGLIBProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CGLIBEchoingTest extends EchoingTestCase {
    protected ProxyFactory createProxyFactory() {
        return new CGLIBProxyFactory();
    }
}
