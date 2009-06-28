package com.thoughtworks.proxy.toys.hotswap;

import com.thoughtworks.proxy.ProxyTestCase;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author Aslak Helles&oslash;y
 */
public class HotSwappingTest extends ProxyTestCase {

    public void testShouldBeAbleToHotSwap() {
        List firstList = new ArrayList();
        firstList.add("first");

        List proxyList = (List)HotSwapping.object(List.class, getFactory(), firstList);
        assertTrue(proxyList.contains("first"));

        List secondList = new ArrayList();
        secondList.add("second");

        Swappable swappable = (Swappable)proxyList;
        assertSame(firstList, swappable.hotswap(secondList));

        assertFalse(proxyList.contains("first"));
        assertTrue(proxyList.contains("second"));
    }

    public void testShouldDiscoverCyclicReferences() {
        List firstList = new ArrayList();
        List hidingOne = (List)HotSwapping.object(List.class, getFactory(), firstList);
        List hidingTwo = (List)HotSwapping.object(List.class, getFactory(), hidingOne);
        List hidingThree = (List)HotSwapping.object(List.class, getFactory(), hidingTwo);

        try {
            ((Swappable)hidingOne).hotswap(hidingThree);
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testShouldNotHotswapRecursively() {
        List list = new ArrayList();
        HashMap map = new HashMap();
        map.put("hello", "world");
        list.add(map);
        List hidingList = (List)HotSwapping.object(List.class, getFactory(), list);
        Object shouldNotBeSwappableMap = hidingList.get(0);
        assertFalse(shouldNotBeSwappableMap instanceof Swappable);
    }

    public void testShouldWorkWithEquals() {
        List hotSwapList = (List)HotSwapping.object(List.class, getFactory(), (Object)null);
        assertFalse(hotSwapList.equals(new ArrayList()));
        assertTrue(hotSwapList.equals(hotSwapList));
    }

    public void testShouldWorkWithHashcode() {
        List hotSwapList = (List)HotSwapping.object(List.class, getFactory(), (Object)null);
        assertTrue(hotSwapList.hashCode() == hotSwapList.hashCode());
    }

    public static interface Screwdriver {
        void screw();
    }

    public static class ScredriverImpl implements Screwdriver, Serializable {
        public void screw() {
            fail("should not be called");
        }
    }

    public static class Person implements Serializable {
        public boolean wasScrewed;

        public void screw() {
            wasScrewed = true;
        }
    }

    public void testShouldForwardToObjectOfDifferentTypeIfTypeForgivingIsTrue() {
        Person person = new Person();
        Screwdriver sd = (Screwdriver)HotSwapping.object(Screwdriver.class, getFactory(), person);
        sd.screw();
        assertTrue(person.wasScrewed);
    }

    private void useSerializedProxy(Screwdriver sd) {
        sd.screw();
        Person person = new Person();
        ((Swappable)sd).hotswap(person);
        sd.screw();
        assertTrue(person.wasScrewed);
    }

    public void testSerializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedProxy((Screwdriver)serializeWithJDK(HotSwapping.object(
                Screwdriver.class, getFactory(), new Person())));
    }

    public void testSerializeWithXStream() {
        useSerializedProxy((Screwdriver)serializeWithXStream(HotSwapping.object(
                Screwdriver.class, getFactory(), new Person())));
    }

    public void testSerializeWithXStreamInPureReflectionMode() {
        useSerializedProxy((Screwdriver)serializeWithXStreamAndPureReflection(HotSwapping.object(
                Screwdriver.class, getFactory(), new Person())));
    }

}