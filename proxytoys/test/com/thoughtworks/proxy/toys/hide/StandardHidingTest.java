package com.thoughtworks.proxy.toys.hide;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class StandardHidingTest extends HidingTest {
    protected ProxyFactory createProxyFactory() {
        return new StandardProxyFactory();
    }
}