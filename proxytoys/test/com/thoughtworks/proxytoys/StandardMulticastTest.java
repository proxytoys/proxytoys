package com.thoughtworks.proxytoys;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class StandardMulticastTest extends MulticastTest {
    protected ProxyFactory createProxyFactory() {
        return new StandardProxyFactory();
    }
}