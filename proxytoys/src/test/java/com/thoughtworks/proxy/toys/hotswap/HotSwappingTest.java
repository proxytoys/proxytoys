package com.thoughtworks.proxy.toys.hotswap;

import com.thoughtworks.proxy.ProxyTestCase;
import static com.thoughtworks.proxy.toys.hotswap.HotSwapping.hotSwappable;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import java.util.List;
import java.util.Map;


/**
 * @author Aslak Helles&oslash;y
 */
public class HotSwappingTest extends ProxyTestCase {

    public void testShouldBeAbleToHotSwap() {
        List<String> firstList = new ArrayList<String>();
        firstList.add("first");

        List proxyList = hotSwappable(List.class).with(firstList).build(getFactory());
        assertTrue(proxyList.contains("first"));

        List<String> secondList = new ArrayList<String>();
        secondList.add("second");

        Swappable swappable = (Swappable) proxyList;
        assertSame(firstList, swappable.hotswap(secondList));

        assertFalse(proxyList.contains("first"));
        assertTrue(proxyList.contains("second"));
    }

    public void testShouldDiscoverCyclicReferences() {
        List firstList = new ArrayList();
        List hidingOne = hotSwappable(List.class).with(firstList).build(getFactory());
        List hidingTwo = hotSwappable(List.class).with(hidingOne).build(getFactory());
        List hidingThree = hotSwappable(List.class).with(hidingTwo).build(getFactory());

        try {
            ((Swappable) hidingOne).hotswap(hidingThree);
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testShouldNotHotswapRecursively() {
        List<Map<String, String>> list = singletonList(singletonMap("hello", "world"));
        List hidingList = hotSwappable(List.class).with(list).build(getFactory());
        Object shouldNotBeSwappableMap = hidingList.get(0);
        assertFalse(shouldNotBeSwappableMap instanceof Swappable);
    }

    public void testShouldWorkWithEquals() {
        List hotSwapList = hotSwappable(List.class).with(null).build(getFactory());
        assertFalse(hotSwapList.equals(new ArrayList()));
        assertTrue(hotSwapList.equals(hotSwapList));
    }

    public void testShouldWorkWithHashcode() {
        List hotSwapList = hotSwappable(List.class).with(null).build(getFactory());
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
        Screwdriver sd = hotSwappable(Screwdriver.class).with(person).build(getFactory());
        sd.screw();
        assertTrue(person.wasScrewed);
    }

    private void useSerializedProxy(Screwdriver sd) {
        sd.screw();
        Person person = new Person();
        ((Swappable) sd).hotswap(person);
        sd.screw();
        assertTrue(person.wasScrewed);
    }

    public void testSerializeWithJDK() throws IOException, ClassNotFoundException {
        Screwdriver sd = hotSwappable(Screwdriver.class).with(new Person()).build(getFactory());
        useSerializedProxy((Screwdriver) serializeWithJDK(sd));
    }

    public void testSerializeWithXStream() {
        Object sd = hotSwappable(Screwdriver.class).with(new Person()).build(getFactory());
        useSerializedProxy((Screwdriver) serializeWithXStream(sd));
    }

    public void testSerializeWithXStreamInPureReflectionMode() {
        Screwdriver sd = hotSwappable(Screwdriver.class).with(new Person()).build(getFactory());
        useSerializedProxy((Screwdriver) serializeWithXStreamAndPureReflection(sd));
    }

}