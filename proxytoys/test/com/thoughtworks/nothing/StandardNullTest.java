package com.thoughtworks.nothing;

import com.thoughtworks.proxytoys.StandardProxyFactory;
import com.thoughtworks.proxytoys.ProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class StandardNullTest extends NullTest {

    protected ProxyFactory createProxyFactory() {
        return new StandardProxyFactory();
    }
}