package com.thoughtworks.proxy.toys.multicast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.thoughtworks.proxy.ProxyTestCase;

/**
 * @author Aslak Helles&oslash;y
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

        Dog timAndTom = (Dog) Multicasting.object(Dog.class, proxyFactory, new Dog[]{tim, tom});
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

        List timAndTom = (List) Multicasting.object(List.class, proxyFactory, new List[]{tim, tom});
        Tail timAndTomsTails = (Tail) timAndTom.get(0);
        timAndTomsTails.wag();

        assertTrue(timsTail.wasWagged());
        assertTrue(tomsTail.wasWagged());
    }

    public void testShouldMulticastRecursivelyForUndeclaredType() {
        TailImpl timsTail = new TailImpl();
        Dog tim = new DogImpl(timsTail);

        TailImpl tomsTail = new TailImpl();
        Dog tom = new DogImpl(tomsTail);

        Dog timAndTom = (Dog) Multicasting.object(proxyFactory, new Dog[]{tim, tom});
        Tail timAndTomsTails = timAndTom.getTail();
        timAndTomsTails.wag();

        assertTrue(timsTail.wasWagged());
        assertTrue(tomsTail.wasWagged());
    }

    public void testShouldFailForIncompatibleTypes() {
        try {
            Multicasting.object(List.class, proxyFactory, new Object[]{new HashMap()});
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testShouldFailForNull() {
        try {
            Multicasting.object(List.class, proxyFactory, new Object[]{null});
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void TODOtestShouldInvokeMethodsInARoundRobinFashion() {
        TailImpl t1 = new TailImpl();
        TailImpl t2 = new TailImpl();
        TailImpl t3 = new TailImpl();
        Tail tail = (Tail) Multicasting.object(new Class[]{Tail.class}, proxyFactory, new Object[]{t1, t2, t3});

        assertFalse(t1.wasWagged());
        assertFalse(t2.wasWagged());
        assertFalse(t3.wasWagged());

        tail.wag();

        assertTrue(t1.wasWagged());
        assertFalse(t2.wasWagged());
        assertFalse(t3.wasWagged());
    }

}