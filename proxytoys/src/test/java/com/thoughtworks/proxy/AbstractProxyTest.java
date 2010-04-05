/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 14-May-2004
 */
package com.thoughtworks.proxy;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * @author Dan North
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Tianshuo Deng
 */
public abstract class AbstractProxyTest {

    /**
     * A publicly settable <tt>ProxyFactory</tt>.
     * <p>
     * The value of this factory is captured by the constructor of each test case, so the class can have a default
     * constructor.
     * </p>
     * <p>
     * Note: by the time the tests run this will have changed, which is why there is an instance variable too.
     * </p>
     *
     * @see com.thoughtworks.proxy.factory.CglibProxyFactory
     * @see com.thoughtworks.proxy.factory.StandardProxyFactory
     */
    public static ProxyFactory PROXY_FACTORY = new StandardProxyFactory();

    /**
     * the actual factory the tests will run against
     */
    private final ProxyFactory proxyFactory;

    protected AbstractProxyTest() {
        proxyFactory = createProxyFactory();
    }

    /**
     * Get a reference to a proxy factory. Override this to force a particular factory.
     */
    protected ProxyFactory createProxyFactory() {
        return PROXY_FACTORY;
    }

    public ProxyFactory getFactory() {
        return proxyFactory;
    }

    protected <T> T serializeWithJDK(T toSerialize) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(outBuffer);
        outStream.writeObject(toSerialize);
        outStream.close();
        ByteArrayInputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
        ObjectInputStream inStream = new ObjectInputStream(inBuffer);
        @SuppressWarnings("unchecked")
        T serialized = (T)inStream.readObject();
        inStream.close();
        assertNotNull(serialized);
        return serialized;
    }

    protected <T> T serializeWithXStream(T toSerialize) {
        final XStream xstream = new XStream(new XppDriver());
        final String xml = xstream.toXML(toSerialize);
        @SuppressWarnings("unchecked")
        T serialized = (T)xstream.fromXML(xml);
        assertNotNull(serialized);
        return serialized;
    }

    protected <T> T serializeWithXStreamAndPureReflection(T toSerialize) {
        final XStream xstream = new XStream(new PureJavaReflectionProvider(), new XppDriver());
        final String xml = xstream.toXML(toSerialize);
        @SuppressWarnings("unchecked")
        T serialized = (T)xstream.fromXML(xml);
        assertNotNull(serialized);
        return serialized;
    }
}