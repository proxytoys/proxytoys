package com.thoughtworks.proxy.toys.nullobject;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class StandardNullTest extends NullTest {

    protected ProxyFactory createProxyFactory() {
        return new StandardProxyFactory();
    }
}