package com.thoughtworks.proxy.toys.hotswap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.Hiding;
import com.thoughtworks.proxy.toys.hotswap.Swappable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CglibHidingTest extends ProxyTestCase {
    protected ProxyFactory createProxyFactory() {
        return new CglibProxyFactory();
    }

    public void testShouldWorkRecursivelyWithMap() {
        List list = new ArrayList();
        HashMap map = new HashMap();
        map.put("hello", "world");
        list.add(map);
        List hidingList = (List) Hiding.object(List.class, getFactory(), list);
        Object shouldBeHidingMap = hidingList.get(0);
        Map hidingMap = (Map) shouldBeHidingMap;
        Swappable swappableMap = (Swappable) hidingMap;
        swappableMap.hotswap(new HashMap());
    }
}