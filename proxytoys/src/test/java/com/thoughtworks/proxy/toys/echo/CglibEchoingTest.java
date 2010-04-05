/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 23-07-2005
 */
package com.thoughtworks.proxy.toys.echo;

import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;


/**
 * @author J&ouml;rg Schaible
 */
public class CglibEchoingTest extends AbstractProxyTest {
    @Override
    protected ProxyFactory createProxyFactory() {
        return new CglibProxyFactory();
    }

    @Test
    public void shouldProxyRealInstance() {
        final StringWriter out = new StringWriter();
        @SuppressWarnings("unchecked")
        final List<File> list = Echoing.proxy(List.class)
            .with(new ArrayList<File>())
            .to(new PrintWriter(out))
            .build(createProxyFactory());
        list.add(new File("."));
        final File file = list.get(0);
        file.exists();
        assertContains("java.io.File.exists()", out);
    }

    private static void assertContains(String expected, Object textObject) {
        String text = textObject.toString();
        assertTrue("Expected [" + expected + "] in text:\n[" + text + "]", text.indexOf(expected) != -1);
    }
}
