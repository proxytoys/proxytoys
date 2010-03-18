/*
 * Copyright (C) 2005 Joerg Schaible
 * Created on 23.07.2005 by Joerg Schaible
 */
package com.thoughtworks.proxy.toys.echo;

import static com.thoughtworks.proxy.toys.echo.Echoing.echoable;
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


public class CglibEchoingTest extends AbstractProxyTest {
    @Override
    protected ProxyFactory createProxyFactory() {
        return new CglibProxyFactory();
    }

    @Test
    public void shouldProxyRealInstance() {
        final StringWriter out = new StringWriter();
        @SuppressWarnings("unchecked")
        final List<File> list = echoable(List.class).with(new ArrayList()).to(new PrintWriter(out)).build(createProxyFactory());
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
