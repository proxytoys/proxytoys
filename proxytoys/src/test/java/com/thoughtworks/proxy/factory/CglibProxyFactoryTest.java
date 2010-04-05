/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 28-Jul-2005
 */
package com.thoughtworks.proxy.factory;

import static junit.framework.Assert.assertFalse;

import org.junit.Test;

import com.thoughtworks.proxy.ProxyFactory;


/**
 * @author J&ouml;rg Schaible
 */
public class CglibProxyFactoryTest {
    @Test
    public void shouldDenyProxyGenerationForFinalClasses() throws Exception {
        ProxyFactory factory = new CglibProxyFactory();
        assertFalse(factory.canProxy(String.class));
    }
}
