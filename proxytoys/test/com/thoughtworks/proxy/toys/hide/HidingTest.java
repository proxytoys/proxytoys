package com.thoughtworks.proxy.toys.hide;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.proxy.toys.hide.HidingInvoker;
import com.thoughtworks.proxy.toys.hide.Swappable;
import com.thoughtworks.proxytoys.ProxyTestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public abstract class HidingTest extends ProxyTestCase {

    public void testShouldBeAbleToHotSwap() {
        List firstList = new ArrayList();
        firstList.add("first");

        List proxyList = (List) HidingInvoker.object(List.class, proxyFactory, firstList);
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
        List hidingOne = (List) HidingInvoker.object(List.class, proxyFactory, firstList);
        List hidingTwo = (List) HidingInvoker.object(List.class, proxyFactory, hidingOne);
        ((Swappable)hidingOne).hotswap(hidingTwo);

        try {
            hidingOne.add("help");
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

}