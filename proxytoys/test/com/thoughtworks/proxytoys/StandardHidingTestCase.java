package com.thoughtworks.proxytoys;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class StandardHidingTestCase extends HidingTest {
    protected ProxyFactory createProxyFactory() {
        return new StandardProxyFactory();
    }
}