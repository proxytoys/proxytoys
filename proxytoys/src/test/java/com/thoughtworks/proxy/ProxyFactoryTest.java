package com.thoughtworks.proxy;

import com.thoughtworks.proxy.kit.Resetter;
import static com.thoughtworks.proxy.toys.nullobject.Null.nullable;
import static junit.framework.Assert.assertFalse;
import org.junit.Test;

import java.io.IOException;


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
        Resetter resetter = nullable(Resetter.class).build();
        assertFalse(resetter.reset(this));
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedFactory((ProxyFactory) serializeWithJDK(getFactory()));
    }

    @Test
    public void serializeWithXStream() {
        useSerializedFactory((ProxyFactory) serializeWithXStream(getFactory()));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        useSerializedFactory((ProxyFactory) serializeWithXStreamAndPureReflection(getFactory()));
    }
}