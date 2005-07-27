package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.ProxyTestCase;
import com.thoughtworks.proxy.factory.CglibProxyFactory;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
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

        public byte getByte() {
            return 2;
        }

        public char getChar() {
            return 2;
        }

        public short getShort() {
            return 2;
        }

        public int getInt() {
            return 2;
        }

        public long getLong() {
            return 2;
        }

        public float getFloat() {
            return 2;
        }

        public double getDouble() {
            return 2;
        }

        public boolean getBoolean() {
            return bool;
        }
    }

    private Primitives primitives;

    protected void setUp() throws Exception {
        super.setUp();
        primitives = (Primitives)Multicasting.object(getFactory(), new Object[]{
                new Primitives(true), new Primitives(true), new Primitives(true)});
    }

    public void testShouldAddBytes() {
        assertEquals(6, primitives.getByte());
    }

    public void testShouldAddChars() {
        assertEquals(6, primitives.getChar());
    }

    public void testShouldAddShorts() {
        assertEquals(6, primitives.getShort());
    }

    public void testShouldAddIntegers() {
        assertEquals(6, primitives.getInt());
    }

    public void testShouldAddLong() {
        assertEquals(6, primitives.getLong());
    }

    public void testShouldAddFloat() {
        assertEquals(6, primitives.getFloat(), 0.0001);
    }

    public void testShouldAddDouble() {
        assertEquals(6, primitives.getDouble(), 0.0001);
    }

    public void testShouldAndBooleans() {
        assertTrue(primitives.getBoolean());
        primitives = (Primitives)Multicasting.object(getFactory(), new Object[]{
                new Primitives(true), new Primitives(true), new Primitives(false)});
        assertFalse(primitives.getBoolean());
    }

}