package com.thoughtworks.proxytoys;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CGLIBFailoverTest extends FailoverTest {
    protected ProxyFactory createProxyFactory() {
        return new CGLIBProxyFactory();
    }
}