/*
 * Created on 28-Jul-2005
 * 
 * (c) 2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
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
