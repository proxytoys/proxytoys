package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.ProxyTestCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision: 1.3 $
 */
public class MulticastTest extends ProxyTestCase {

    public static interface Dog {
        Tail getTail();
    }

    public static interface Tail {
        void wag();
    }

    public static class DogImpl implements Dog {
        private final Tail tail;

        public DogImpl(Tail tail) {
            this.tail = tail;
        }

        public Tail getTail() {
            return tail;
        }
    }

    public static class TailImpl implements Tail {
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

    public void testShouldMulticastRecursivelyForDeclaredReturnType() {
        TailImpl timsTail = new TailImpl();
        Dog tim = new DogImpl(timsTail);

        TailImpl tomsTail = new TailImpl();
        Dog tom = new DogImpl(tomsTail);

        Dog timAndTom = (Dog)Multicasting.object(Dog.class, getFactory(), new Dog[]{tim, tom});
        Tail timAndTomsTails = timAndTom.getTail();
        timAndTomsTails.wag();

        assertTrue(timsTail.wasWagged());
        assertTrue(tomsTail.wasWagged());
    }

    public void testShouldMulticastRecursivelyForRuntimeReturnType() {
        List tim = new ArrayList();
        TailImpl timsTail = new TailImpl();
        tim.add(timsTail);

        List tom = new ArrayList();
        OtherTailImpl tomsTail = new OtherTailImpl();
        tom.add(tomsTail);

        List timAndTom = (List)Multicasting.object(List.class, getFactory(), new List[]{tim, tom});
        Tail timAndTomsTails = (Tail)timAndTom.get(0);
        timAndTomsTails.wag();

        assertTrue(timsTail.wasWagged());
        assertTrue(tomsTail.wasWagged());
    }

    public void testShouldMulticastRecursivelyForUndeclaredType() {
        TailImpl timsTail = new TailImpl();
        Dog tim = new DogImpl(timsTail);

        TailImpl tomsTail = new TailImpl();
        Dog tom = new DogImpl(tomsTail);

        Dog timAndTom = (Dog)Multicasting.object(getFactory(), new Dog[]{tim, tom});
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

    public void testShouldMulticastToIncompatibleTypes() {
        NotMap list = new NotMapImpl();
        Map map = new HashMap();
        Object multicast = Multicasting.object(getFactory(), new Object[]{list, map});
        ((NotMap)multicast).add("hello");
        ((Map)multicast).put("hello", "world");
        assertEquals("hello", list.get(0));
        assertEquals("world", map.get("hello"));
    }

    public void testShouldNotReturnProxyWhenThereIsOnlyOneForUndeclaredReturnType() {
        Map map = new HashMap();
        ProxyFactory factory = getFactory();
        Object multicast = Multicasting.object(factory, new Object[]{map});
        assertFalse(factory.isProxyClass(multicast.getClass()));
        assertSame(map, multicast);
    }

    public void testShouldNotReturnProxyWhenThereIsOnlyOneForCompatibleDeclaredReturnTypes() {
        Map map = new HashMap();
        ProxyFactory factory = getFactory();
        Object multicast = Multicasting.object(new Class[]{Map.class, Serializable.class}, factory, new Object[]{map});
        assertFalse(factory.isProxyClass(multicast.getClass()));
        assertSame(map, multicast);
    }

    public void TODOtestShouldInvokeMethodsInARoundRobinFashion() {
        TailImpl t1 = new TailImpl();
        TailImpl t2 = new TailImpl();
        TailImpl t3 = new TailImpl();
        Tail tail = (Tail)Multicasting.object(new Class[]{Tail.class}, getFactory(), new Object[]{t1, t2, t3});

        assertFalse(t1.wasWagged());
        assertFalse(t2.wasWagged());
        assertFalse(t3.wasWagged());

        tail.wag();

        assertTrue(t1.wasWagged());
        assertFalse(t2.wasWagged());
        assertFalse(t3.wasWagged());
    }

}