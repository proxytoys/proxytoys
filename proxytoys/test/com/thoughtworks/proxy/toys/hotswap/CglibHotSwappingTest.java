package com.thoughtworks.proxy.toys.hotswap;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.factory.CglibProxyFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CglibHotSwappingTest extends ProxyTestCase {
    protected ProxyFactory createProxyFactory() {
        return new CglibProxyFactory();
    }

    public void testShouldWorkRecursivelyWithMap() {
        List list = new ArrayList();
        HashMap map = new HashMap();
        map.put("hello", "world");
        list.add(map);
        List hidingList = (List) HotSwapping.object(List.class, getFactory(), list);
        Object shouldBeHidingMap = hidingList.get(0);
        Map hidingMap = (Map) shouldBeHidingMap;
        Swappable swappableMap = (Swappable) hidingMap;
        swappableMap.hotswap(new HashMap());
    }
}