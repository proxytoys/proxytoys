package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.ProxyFactory;
import static com.thoughtworks.proxy.toys.multicast.Multicasting.multicastable;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class MulticastTest extends ProxyTestCase {

    public static interface Dog {
        Tail getTail();
    }

    public static interface Tail {
        void wag();

        boolean wasWagged();
    }

    public static class DogImpl implements Dog, Serializable {
        private Tail tail;

        public DogImpl(Tail tail) {
            this.tail = tail;
        }

        public Tail getTail() {
            return tail;
        }
    }

    public static class TailImpl implements Tail, Serializable {
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

        Dog timAndTom = (Dog) multicastable(Dog.class).with(tim, tom).build(getFactory());
        Tail timAndTomsTails = timAndTom.getTail();
        timAndTomsTails.wag();

        assertTrue(timsTail.wasWagged());
        assertTrue(tomsTail.wasWagged());
    }

    @Test
    public void shouldMulticastRecursivelyForRuntimeReturnType() {
        List tim = new ArrayList();
        TailImpl timsTail = new TailImpl();
        tim.add(timsTail);

        List tom = new ArrayList();
        OtherTailImpl tomsTail = new OtherTailImpl();
        tom.add(tomsTail);

        List timAndTom = (List) multicastable(List.class).with(tim, tom).build(getFactory());
        Tail timAndTomsTails = (Tail) timAndTom.get(0);
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

        Dog timAndTom = (Dog) multicastable(tim, tom).build(getFactory());
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
        private List values = new ArrayList();

        public void add(Object s) {
            values.add(s);
        }

        public Object get(int i) {
            return values.get(i);
        }
    }

    @Test
    public void shouldMulticastToIncompatibleTypes() {
        NotMap list = new NotMapImpl();
        Map map = new HashMap();
        Object multicast = multicastable(list, map).build(getFactory());
        ((NotMap) multicast).add("hello");
        ((Map) multicast).put("hello", "world");
        assertEquals("hello", list.get(0));
        assertEquals("world", map.get("hello"));
    }

    @Test
    public void shouldNotReturnProxyWhenThereIsOnlyOneForUndeclaredReturnType() {
        Map map = new HashMap();
        ProxyFactory factory = getFactory();
        Object multicast = multicastable(map).build(factory);
        assertFalse(factory.isProxyClass(multicast.getClass()));
        assertSame(map, multicast);
    }

    @Test
    public void shouldNotReturnProxyWhenThereIsOnlyOneForCompatibleDeclaredReturnTypes() {
        Map map = new HashMap();
        ProxyFactory factory = getFactory();
        Object multicast = multicastable(Map.class, Serializable.class).with(map).build(factory);
        assertFalse(factory.isProxyClass(multicast.getClass()));
        assertSame(map, multicast);
    }

    @Test
    public void shouldCallDirectMethodForFinalTargets() throws NoSuchMethodException {
        Method method = StringBuffer.class.getMethod("append", String.class);
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        Multicast multicast = (Multicast) multicastable(buffer1, buffer2).build(getFactory());
        multicast.multicastTargets(method, new Object[]{"JUnit"});
        assertEquals("JUnit", buffer1.toString());
        assertEquals("JUnit", buffer2.toString());
    }

    @Test
    public void shouldCallAMatchingMethodForFinalTargets() throws NoSuchMethodException {
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        Multicast multicast = (Multicast) multicastable(buffer1, buffer2).build(getFactory());
        multicast.multicastTargets(StringBuffer.class, "append", new Object[]{"JUnit"});
        assertEquals("JUnit", buffer1.toString());
        assertEquals("JUnit", buffer2.toString());
    }

    @Test
    public void shouldThrowNoSuchMethodExceptionForANonMatchingCall() {
        Multicast multicast = (Multicast) multicastable(
                new StringBuffer(), new StringBuffer()).build(getFactory());
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
        Multicast multicast = (Multicast) multicastable(buffer1, buffer2).build(getFactory());
        StringBuffer[] buffers = (StringBuffer[]) multicast.getTargetsInArray(StringBuffer.class);
        assertSame(buffer1, buffers[0]);
        assertSame(buffer2, buffers[1]);
    }

    @Test
    public void shouldReturnTargetsInArray() throws Exception {
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        Multicast multicast = (Multicast) multicastable(buffer1, buffer2).build(getFactory());
        Object[] buffers = multicast.getTargetsInArray();
        assertSame(buffer1, buffers[0]);
        assertSame(buffer2, buffers[1]);
    }

    // joehni: IMHO not a valid test case, looks more like an mix of failover and pool
    public void TODOtestShouldInvokeMethodsInARoundRobinFashion() {
        TailImpl t1 = new TailImpl();
        TailImpl t2 = new TailImpl();
        TailImpl t3 = new TailImpl();
        Tail tail = (Tail) multicastable(Tail.class).with(t1, t2, t3).build(getFactory());

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

        Dog timAndTom = (Dog) multicastable(Dog.class).with(tim, tom).build(getFactory());
        return timAndTom.getTail();
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedProxy((Tail) serializeWithJDK(prepareTimAndTimsTail()));
    }

    @Test
    public void serializeWithXStream() {
        useSerializedProxy((Tail) serializeWithXStream(prepareTimAndTimsTail()));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        useSerializedProxy((Tail) serializeWithXStreamAndPureReflection(prepareTimAndTimsTail()));
    }

}