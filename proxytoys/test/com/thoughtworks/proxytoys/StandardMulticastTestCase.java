package com.thoughtworks.proxytoys;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class StandardMulticastTestCase extends MulticastTest {
    protected ProxyFactory createProxyFactory() {
        return new StandardProxyFactory();
    }
}