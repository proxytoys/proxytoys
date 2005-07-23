/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 23.07.2005 by Jörg Schaible
 */
package com.thoughtworks.proxy.toys.echo;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.factory.CglibProxyFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public class CglibEchoingTestCase extends ProxyTestCase {
    protected ProxyFactory createProxyFactory() {
        return new CglibProxyFactory();
    }

    public void testShouldProxyRealInstance() {
        final StringWriter out = new StringWriter();
        final List list = (List)Echoing.object(List.class, new ArrayList(), new PrintWriter(out), createProxyFactory());
        list.add(new File("."));
        final File file = (File)list.get(0);
        file.exists();
        assertContains("java.io.File.exists()", out);
    }

    private static void assertContains(String expected, Object textObject) {
        String text = textObject.toString();
        assertTrue("Expected [" + expected + "] in text:\n[" + text + "]", text.indexOf(expected) != -1);
    }
}
