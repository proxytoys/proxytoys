package com.thoughtworks.proxy;

import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.XppDriver;

import org.jmock.MockObjectTestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * @author Dan North
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public abstract class ProxyTestCase extends MockObjectTestCase {
    /**
     * A publicly settable <tt>ProxyFactory</tt>.
     * <p>
     * The value of this factory is captured by the constructor of each test case, so the class can have a default constructor.
     * </p>
     * <p>
     * Note: by the time the tests run this will have changed, which is why there is an instance variable too.
     * </p>
     * 
     * @see com.thoughtworks.proxy.factory.CglibProxyFactory
     * @see com.thoughtworks.proxy.factory.StandardProxyFactory
     * @see AllTests#suite()
     */
    public static ProxyFactory PROXY_FACTORY = new StandardProxyFactory();

    /** the actual factory the tests will run against */
    private final ProxyFactory proxyFactory;

    protected ProxyTestCase() {
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
    
    protected Object serializeWithJDK(Object toSerialize) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(outBuffer);
        outStream.writeObject(toSerialize);
        outStream.close();
        ByteArrayInputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
        ObjectInputStream inStream = new ObjectInputStream(inBuffer);
        Object serialized = inStream.readObject();
        inStream.close();
        assertNotNull(serialized);
        return serialized;
    }
    
    protected Object serializeWithXStream(Object toSerialize) throws IOException, ClassNotFoundException {
        final XStream xstream = new XStream(new XppDriver());
        final String xml = xstream.toXML(toSerialize);
        Object serialized = xstream.fromXML(xml);
        assertNotNull(serialized);
        return serialized;
    }
    
    protected Object serializeWithXStreamAndPureReflection(Object toSerialize) throws IOException, ClassNotFoundException {
        final XStream xstream = new XStream(new PureJavaReflectionProvider(), new XppDriver());
        final String xml = xstream.toXML(toSerialize);
        Object serialized = xstream.fromXML(xml);
        assertNotNull(serialized);
        return serialized;
    }
}