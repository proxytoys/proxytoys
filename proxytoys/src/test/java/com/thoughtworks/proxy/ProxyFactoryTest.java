package com.thoughtworks.proxy;

import com.thoughtworks.proxy.kit.Resetter;
import static com.thoughtworks.proxy.toys.nullobject.Null.nullable;

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
        Resetter resetter = (Resetter) nullable(Resetter.class).build();
        assertFalse(resetter.reset(this));
    }

    public void testSerializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedFactory((ProxyFactory) serializeWithJDK(getFactory()));
    }

    public void testSerializeWithXStream() {
        useSerializedFactory((ProxyFactory) serializeWithXStream(getFactory()));
    }

    public void testSerializeWithXStreamInPureReflectionMode() {
        useSerializedFactory((ProxyFactory) serializeWithXStreamAndPureReflection(getFactory()));
    }
}