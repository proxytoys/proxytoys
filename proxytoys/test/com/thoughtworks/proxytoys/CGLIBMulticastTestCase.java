package com.thoughtworks.proxytoys;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CGLIBMulticastTestCase extends MulticastTest {
    protected ProxyFactory createProxyFactory() {
        return new CGLIBProxyFactory();
    }

    public static class Primitives {
        private final boolean bool;

        public Primitives(boolean bool) {
            this.bool = bool;
        }

        public int getInt() {
            return 3;
        }

        public boolean getBoolean() {
            return bool;
        }
    }

    public void testShouldAddIntegers() {
        Primitives primitives = (Primitives) Multicast.object(proxyFactory, new Object[]{new Primitives(true), new Primitives(true), new Primitives(true)});
        assertEquals(9, primitives.getInt());
    }

    public void testShouldAndBooleans() {
        Primitives primitives = (Primitives) Multicast.object(proxyFactory, new Object[]{new Primitives(true), new Primitives(false), new Primitives(false)});
        assertFalse(primitives.getBoolean());
    }

}