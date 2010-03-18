package com.thoughtworks.proxy.toys.hotswap;

import static com.thoughtworks.proxy.toys.hotswap.HotSwapping.hotSwappable;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;


/**
 * @author Aslak Helles&oslash;y
 */
public class HotSwappingTest extends AbstractProxyTest {
    @Test
    public void shouldBeAbleToHotSwap() {
        List<String> firstList = new ArrayList<String>();
        firstList.add("first");

        @SuppressWarnings("unchecked")
        List<String> proxyList = hotSwappable(List.class).with(firstList).build(getFactory());
        assertTrue(proxyList.contains("first"));

        List<String> secondList = new ArrayList<String>();
        secondList.add("second");

        Swappable swappable = (Swappable) proxyList;
        assertSame(firstList, swappable.hotswap(secondList));

        assertFalse(proxyList.contains("first"));
        assertTrue(proxyList.contains("second"));
    }

    @Test
    public void shouldDiscoverCyclicReferences() {
        List<?> firstList = new ArrayList<Object>();
        List<?> hidingOne = hotSwappable(List.class).with(firstList).build(getFactory());
        List<?> hidingTwo = hotSwappable(List.class).with(hidingOne).build(getFactory());
        List<?> hidingThree = hotSwappable(List.class).with(hidingTwo).build(getFactory());

        try {
            Swappable.class.cast(hidingOne).hotswap(hidingThree);
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void shouldNotHotswapRecursively() {
        List<Map<String, String>> list = singletonList(singletonMap("hello", "world"));
        @SuppressWarnings("unchecked")
        List<Map<String, String>> hidingList = hotSwappable(List.class).with(list).build(getFactory());
        Map<String, String> shouldNotBeSwappableMap = hidingList.get(0);
        assertFalse(shouldNotBeSwappableMap instanceof Swappable);
    }

    @Test
    public void shouldWorkWithEquals() {
        List<?> hotSwapList = hotSwappable(List.class).with(null).build(getFactory());
        assertFalse(hotSwapList.equals(new ArrayList<Object>()));
        assertTrue(hotSwapList.equals(hotSwapList));
    }

    @Test
    public void shouldWorkWithHashcode() {
        List<?> hotSwapList = hotSwappable(List.class).with(null).build(getFactory());
        assertTrue(hotSwapList.hashCode() == hotSwapList.hashCode());
    }

    public static interface Screwdriver {
        void screw();
    }

    @SuppressWarnings("serial")
    public static class ScrewdriverImpl implements Screwdriver, Serializable {
        public void screw() {
            fail("should not be called");
        }
    }

    @SuppressWarnings("serial")
    public static class Person implements Serializable {
        public boolean wasScrewed;

        public void screw() {
            wasScrewed = true;
        }
    }

    @Test
    public void shouldForwardToObjectOfDifferentTypeIfTypeForgivingIsTrue() {
        Person person = new Person();
        Screwdriver sd = hotSwappable(Screwdriver.class).with(person).build(getFactory());
        sd.screw();
        assertTrue(person.wasScrewed);
    }

    private void useSerializedProxy(Screwdriver sd) {
        sd.screw();
        Person person = new Person();
        Swappable.class.cast(sd).hotswap(person);
        sd.screw();
        assertTrue(person.wasScrewed);
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        Screwdriver sd = hotSwappable(Screwdriver.class).with(new Person()).build(getFactory());
        useSerializedProxy(Screwdriver.class.cast(serializeWithJDK(sd)));
    }

    @Test
    public void serializeWithXStream() {
        Object sd = hotSwappable(Screwdriver.class).with(new Person()).build(getFactory());
        useSerializedProxy(Screwdriver.class.cast(serializeWithXStream(sd)));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        Screwdriver sd = hotSwappable(Screwdriver.class).with(new Person()).build(getFactory());
        useSerializedProxy(Screwdriver.class.cast(serializeWithXStreamAndPureReflection(sd)));
    }
}