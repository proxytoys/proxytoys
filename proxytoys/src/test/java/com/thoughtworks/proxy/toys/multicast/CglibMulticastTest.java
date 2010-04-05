/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17-May-2004
 */
package com.thoughtworks.proxy.toys.multicast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class CglibMulticastTest extends AbstractProxyTest {

    @Override
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

    @Before
    public void setUp() throws Exception {
        primitives = Multicasting.proxy(Primitives.class)
            .with(new Primitives(true), new Primitives(true), new Primitives(true))
            .build(getFactory());
    }

    @Test
    public void shouldAddBytes() {
        assertEquals(6, primitives.getByte());
    }

    @Test
    public void shouldAddChars() {
        assertEquals(6, primitives.getChar());
    }

    @Test
    public void shouldAddShorts() {
        assertEquals(6, primitives.getShort());
    }

    @Test
    public void shouldAddIntegers() {
        assertEquals(6, primitives.getInt());
    }

    @Test
    public void shouldAddLong() {
        assertEquals(6, primitives.getLong());
    }

    @Test
    public void shouldAddFloat() {
        assertEquals(6, primitives.getFloat(), 0.0001);
    }

    @Test
    public void shouldAddDouble() {
        assertEquals(6, primitives.getDouble(), 0.0001);
    }

    @Test
    public void shouldAndBooleans() {
        assertTrue(primitives.getBoolean());
        primitives = Primitives.class.cast(Multicasting.proxy(
                new Primitives(true), new Primitives(true), new Primitives(false)).build(getFactory()));
        assertFalse(primitives.getBoolean());
    }

}