/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17-May-2004
 */
package com.thoughtworks.proxy;

import static junit.framework.Assert.assertFalse;

import java.io.IOException;

import org.junit.Test;

import com.thoughtworks.proxy.kit.Resetter;
import com.thoughtworks.proxy.toys.nullobject.Null;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class ProxyFactoryTest extends AbstractProxyTest {
    @Test
    public void shouldNotBeAbleToProxyVoidClass() {
        assertFalse(getFactory().canProxy(Void.class));
        assertFalse(getFactory().canProxy(void.class));
    }

    private void useSerializedFactory(ProxyFactory factory) {
        @SuppressWarnings("unchecked")
        Resetter<ProxyFactoryTest> resetter = Null.proxy(Resetter.class).build();
        assertFalse(resetter.reset(this));
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedFactory(ProxyFactory.class.cast(serializeWithJDK(getFactory())));
    }

    @Test
    public void serializeWithXStream() {
        useSerializedFactory(ProxyFactory.class.cast(serializeWithXStream(getFactory())));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        useSerializedFactory(ProxyFactory.class.cast(serializeWithXStreamAndPureReflection(getFactory())));
    }
}