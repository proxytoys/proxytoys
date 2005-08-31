package com.thoughtworks.proxy;

import com.thoughtworks.proxy.kit.Resetter;
import com.thoughtworks.proxy.toys.nullobject.Null;

import java.io.IOException;

/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class ProxyFactoryTest extends ProxyTestCase {
    public void testShouldNotBeAbleToProxyVoidClass() {
        assertFalse(getFactory().canProxy(Void.class));
        assertFalse(getFactory().canProxy(void.class));
    }
    
    private void useSerializedFactory(ProxyFactory factory) {
        Resetter resetter = (Resetter)Null.object(Resetter.class);
        assertFalse(resetter.reset(this));
    }

    public void testSerializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedFactory((ProxyFactory)serializeWithJDK(getFactory()));
    }

    public void testSerializeWithXStream() throws IOException, ClassNotFoundException {
        useSerializedFactory((ProxyFactory)serializeWithXStream(getFactory()));
    }

    public void testSerializeWithXStreamInPureReflectionMode() throws IOException, ClassNotFoundException {
        useSerializedFactory((ProxyFactory)serializeWithXStreamAndPureReflection(getFactory()));
    }
}