package com.thoughtworks.proxy.toys.hotswap;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;
import com.thoughtworks.proxy.toys.hotswap.Swappable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class HotSwappingTest extends ProxyTestCase {

    public void testShouldBeAbleToHotSwap() {
        List firstList = new ArrayList();
        firstList.add("first");

        List proxyList = (List) HotSwapping.object(List.class, getFactory(), firstList);
        assertTrue(proxyList.contains("first"));

        List secondList = new ArrayList();
        secondList.add("second");

        Swappable swappable = (Swappable) proxyList;
        assertSame(firstList, swappable.hotswap(secondList));

        assertFalse(proxyList.contains("first"));
        assertTrue(proxyList.contains("second"));
    }

    public void testShouldDiscoverCyclicReferences() {
        List firstList = new ArrayList();
        List hidingOne = (List) HotSwapping.object(List.class, getFactory(), firstList);
        List hidingTwo = (List) HotSwapping.object(List.class, getFactory(), hidingOne);
        ((Swappable)hidingOne).hotswap(hidingTwo);

        try {
            hidingOne.add("help");
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

}