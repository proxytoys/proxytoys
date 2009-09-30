/*
 * Created on 28-Jul-2005
 * 
 * (c) 2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.factory;

import com.thoughtworks.proxy.ProxyFactory;
import junit.framework.TestCase;
import static junit.framework.Assert.assertFalse;
import org.junit.Test;


/**
 * @author J&ouml;rg Schaible
 */
public class CglibProxyFactoryTest {
     @Test
    public void testShouldDenyProxyGenerationForFinalClasses() throws Exception {
        ProxyFactory factory = new CglibProxyFactory();
        assertFalse(factory.canProxy(String.class));
    }
}
