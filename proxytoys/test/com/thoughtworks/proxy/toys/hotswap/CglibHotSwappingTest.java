package com.thoughtworks.proxy.toys.hotswap;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.factory.CglibProxyFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CglibHotSwappingTest extends ProxyTestCase {
    protected ProxyFactory createProxyFactory() {
        return new CglibProxyFactory();
    }

    public void testShouldNotHotswapRecursively() {
        List list = new ArrayList();
        HashMap map = new HashMap();
        map.put("hello", "world");
        list.add(map);
        List hidingList = (List) HotSwapping.object(List.class, getFactory(), list);
        Object shouldNotBeSwappableMap = hidingList.get(0);
        assertFalse(shouldNotBeSwappableMap instanceof Swappable);
    }

    public static class Yin {
        private final Yang yang;

        public Yin(Yang yang) {
            this.yang = yang;
        }

        public Yang getYang() {
            return yang;
        }
    }

    public static class Yang {
        private final Yin yin;

        public Yang(Yin yin) {
            this.yin = yin;
        }

        public Yin getYin() {
            return yin;
        }
    }

    public void testShouldMakeMutualDependenciesPossible() {
        Yin yin = (Yin) HotSwapping.object(Yin.class, getFactory(), null);
        Yang realYang = new Yang(yin);
        Yin realYin = new Yin(realYang);
        ((Swappable) yin).hotswap(realYin);

        assertTrue(yin.equals(realYang.getYin()));
        assertTrue(realYang.equals(yin.getYang()));
    }

}