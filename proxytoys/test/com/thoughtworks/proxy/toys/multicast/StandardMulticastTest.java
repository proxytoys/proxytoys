package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class StandardMulticastTest extends MulticastTestCase {
    protected ProxyFactory createProxyFactory() {
        return new StandardProxyFactory();
    }
}