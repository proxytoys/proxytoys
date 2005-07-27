package com.thoughtworks.proxy;

/**
 * @author Aslak Helles&oslash;y
 */
public class ProxyFactoryTest extends ProxyTestCase {
    public void testShouldNotBeAbleToProxyVoidClass() {
        assertFalse(getFactory().canProxy(Void.class));
        assertFalse(getFactory().canProxy(void.class));
    }

}