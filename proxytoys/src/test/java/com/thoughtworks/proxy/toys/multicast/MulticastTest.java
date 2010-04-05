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
package com.thoughtworks.proxy.toys.multicast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;
import com.thoughtworks.proxy.ProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class MulticastTest extends AbstractProxyTest {

    public static interface Dog {
        Tail getTail();
    }

    public static interface Tail {
        void wag();

        boolean wasWagged();
    }

    public static class DogImpl implements Dog, Serializable {
        private static final long serialVersionUID = 1L;
        private Tail tail;

        public DogImpl(Tail tail) {
            this.tail = tail;
        }

        public Tail getTail() {
            return tail;
        }
    }

    public static class TailImpl implements Tail, Serializable {
        private static final long serialVersionUID = 1L;
        private boolean wagged = false;

        public void wag() {
            wagged = true;
        }

        public boolean wasWagged() {
            return wagged;
        }
    }

    public static class OtherTailImpl implements Tail {
        private boolean wagged = false;

        public void wag() {
            wagged = true;
        }

        public boolean wasWagged() {
            return wagged;
        }
    }

    @Test
    public void shouldMulticastRecursivelyForDeclaredReturnType() {
        TailImpl timsTail = new TailImpl();
        Dog tim = new DogImpl(timsTail);

        TailImpl tomsTail = new TailImpl();
        Dog tom = new DogImpl(tomsTail);

        Dog timAndTom = Multicasting.proxy(Dog.class).with(tim, tom).build(getFactory());
        Tail timAndTomsTails = timAndTom.getTail();
        timAndTomsTails.wag();

        assertTrue(timsTail.wasWagged());
        assertTrue(tomsTail.wasWagged());
    }

    @Test
    public void shouldMulticastRecursivelyForRuntimeReturnType() {
        List<Tail> tim = new ArrayList<Tail>();
        TailImpl timsTail = new TailImpl();
        tim.add(timsTail);

        List<Tail> tom = new ArrayList<Tail>();
        OtherTailImpl tomsTail = new OtherTailImpl();
        tom.add(tomsTail);

        @SuppressWarnings("unchecked")
        List<Tail> timAndTom = Multicasting.proxy(List.class).with(tim, tom).build(getFactory());
        Tail timAndTomsTails = timAndTom.get(0);
        timAndTomsTails.wag();

        assertTrue(timsTail.wasWagged());
        assertTrue(tomsTail.wasWagged());
    }

    @Test
    public void shouldMulticastRecursivelyForUndeclaredType() {
        TailImpl timsTail = new TailImpl();
        Dog tim = new DogImpl(timsTail);

        TailImpl tomsTail = new TailImpl();
        Dog tom = new DogImpl(tomsTail);

        Dog timAndTom = Dog.class.cast(Multicasting.proxy(tim, tom).build(getFactory()));
        Tail timAndTomsTails = timAndTom.getTail();
        timAndTomsTails.wag();

        assertTrue(timsTail.wasWagged());
        assertTrue(tomsTail.wasWagged());
    }

    public static interface NotMap {
        void add(Object s);

        Object get(int i);
    }

    public static class NotMapImpl implements NotMap {
        private List<Object> values = new ArrayList<Object>();

        public void add(Object s) {
            values.add(s);
        }

        public Object get(int i) {
            return values.get(i);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMulticastToIncompatibleTypes() {
        NotMap list = new NotMapImpl();
        Map<String, String> map = new HashMap<String, String>();
        Object multicast = Multicasting.proxy(list, map).build(getFactory());
        NotMap.class.cast(multicast).add("hello");
        Map.class.cast(multicast).put("hello", "world");
        assertEquals("hello", list.get(0));
        assertEquals("world", map.get("hello"));
    }

    @Test
    public void shouldNotReturnProxyWhenThereIsOnlyOneForUndeclaredReturnType() {
        Map<?, ?> map = new HashMap<Object, Object>();
        ProxyFactory factory = getFactory();
        Object multicast = Multicasting.proxy(map).build(factory);
        assertFalse(factory.isProxyClass(multicast.getClass()));
        assertSame(map, multicast);
    }

    @Test
    public void shouldNotReturnProxyWhenThereIsOnlyOneForCompatibleDeclaredReturnTypes() {
        Map<?, ?> map = new HashMap<Object, Object>();
        ProxyFactory factory = getFactory();
        Map<?,?> multicast = Multicasting.proxy(Map.class, Serializable.class).with(map).build(factory);
        assertFalse(factory.isProxyClass(multicast.getClass()));
        assertSame(map, multicast);
    }

    @Test
    public void shouldCallDirectMethodForFinalTargets() throws NoSuchMethodException {
        Method method = StringBuffer.class.getMethod("append", String.class);
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        Multicast multicast = Multicasting.proxy(buffer1, buffer2).build(getFactory());
        multicast.multicastTargets(method, new Object[]{"JUnit"});
        assertEquals("JUnit", buffer1.toString());
        assertEquals("JUnit", buffer2.toString());
    }

    @Test
    public void shouldCallAMatchingMethodForFinalTargets() throws NoSuchMethodException {
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        Multicast multicast = Multicasting.proxy(buffer1, buffer2).build(getFactory());
        multicast.multicastTargets(StringBuffer.class, "append", new Object[]{"JUnit"});
        assertEquals("JUnit", buffer1.toString());
        assertEquals("JUnit", buffer2.toString());
    }

    @Test
    public void shouldThrowNoSuchMethodExceptionForANonMatchingCall() {
        Multicast multicast = Multicasting.proxy(new StringBuffer(), new StringBuffer()).build(getFactory());
        try {
            multicast.multicastTargets(StringBuffer.class, "toString", new Object[]{"JUnit", 5});
            fail(NoSuchMethodException.class.getName() + " expected");
        } catch (NoSuchMethodException e) {
            assertEquals(e.getMessage(), StringBuffer.class.getName()
                    + ".toString(java.lang.String, java.lang.Integer)");
        }
    }

    @Test
    public void shouldReturnTargetsInTypedArray() throws Exception {
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        Multicast multicast = Multicasting.proxy(buffer1, buffer2).build(getFactory());
        StringBuffer[] buffers = multicast.getTargetsInArray(StringBuffer.class);
        assertSame(buffer1, buffers[0]);
        assertSame(buffer2, buffers[1]);
    }

    @Test
    public void shouldReturnTargetsInArray() throws Exception {
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        Multicast multicast = Multicasting.proxy(buffer1, buffer2).build(getFactory());
        Object[] buffers = multicast.getTargetsInArray();
        assertSame(buffer1, buffers[0]);
        assertSame(buffer2, buffers[1]);
    }

    // joehni: IMHO not a valid test case, looks more like an mix of failover and pool
    public void TODOtestShouldInvokeMethodsInARoundRobinFashion() {
        TailImpl t1 = new TailImpl();
        TailImpl t2 = new TailImpl();
        TailImpl t3 = new TailImpl();
        Tail tail = Multicasting.proxy(Tail.class).with(t1, t2, t3).build(getFactory());

        assertFalse(t1.wasWagged());
        assertFalse(t2.wasWagged());
        assertFalse(t3.wasWagged());

        tail.wag();

        assertTrue(t1.wasWagged());
        assertFalse(t2.wasWagged());
        assertFalse(t3.wasWagged());
    }

    private void useSerializedProxy(Tail tail) {
        assertFalse(tail.wasWagged());
        tail.wag();
        assertTrue(tail.wasWagged());
        Multicast multicast = (Multicast) tail;
        assertEquals(2, multicast.getTargetsInArray().length);
    }

    private Tail prepareTimAndTimsTail() {
        TailImpl timsTail = new TailImpl();
        Dog tim = new DogImpl(timsTail);

        TailImpl tomsTail = new TailImpl();
        Dog tom = new DogImpl(tomsTail);

        Dog timAndTom = Multicasting.proxy(Dog.class).with(tim, tom).build(getFactory());
        return timAndTom.getTail();
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedProxy(serializeWithJDK(prepareTimAndTimsTail()));
    }

    @Test
    public void serializeWithXStream() {
        useSerializedProxy(serializeWithXStream(prepareTimAndTimsTail()));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        useSerializedProxy(serializeWithXStreamAndPureReflection(prepareTimAndTimsTail()));
    }

}