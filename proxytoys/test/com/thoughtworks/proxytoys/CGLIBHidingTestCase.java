package com.thoughtworks.proxytoys;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CGLIBHidingTestCase extends HidingTest {
    protected ProxyFactory createProxyFactory() {
        return new CGLIBProxyFactory();
    }

    public void testShouldWorkRecursivelyWithMap() {
        List list = new ArrayList();
        HashMap map = new HashMap();
        map.put("hello", "world");
        list.add(map);
        List hidingList = (List) Hiding.object(List.class, proxyFactory, list);
        Object shouldBeHidingMap = hidingList.get(0);
        Map hidingMap = (Map) shouldBeHidingMap;
        Swappable swappableMap = (Swappable) hidingMap;
        swappableMap.hotswap(new HashMap());
    }

}