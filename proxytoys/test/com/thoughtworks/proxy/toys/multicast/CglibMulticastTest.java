package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.factory.CglibProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CglibMulticastTest extends ProxyTestCase {
    protected ProxyFactory createProxyFactory() {
        return new CglibProxyFactory();
    }

    public static class Primitives {
        private final boolean bool;

        public Primitives(boolean bool) {
            this.bool = bool;
        }

        public int getInt() {
            return 2;
        }

        public boolean getBoolean() {
            return bool;
        }
    }

    public void testShouldAddIntegers() {
        Primitives primitives = (Primitives) Multicasting.object(getFactory(), new Object[]{new Primitives(true), new Primitives(true), new Primitives(true)});
        assertEquals(6, primitives.getInt());
    }

    public void testShouldAndBooleans() {
        Primitives primitives = (Primitives) Multicasting.object(getFactory(), new Object[]{new Primitives(true), new Primitives(false), new Primitives(false)});
        assertFalse(primitives.getBoolean());
    }

}