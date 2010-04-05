/*
 * (c) 2004-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 14-May-2004
 */
package com.thoughtworks.proxy.toys.nullobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 */
public class CglibNullTest extends AbstractProxyTest {

    public static class ClassWithPrimitiveParametersInConstructor {
        private boolean bo;
        private byte by;
        private char ch;
        private int in;
        private long lo;
        private float fl;
        private double db;

        public ClassWithPrimitiveParametersInConstructor(
                boolean bo, byte by, char ch, int in, long lo, float fl, double db) {
            assertEquals(this.bo, bo);
            assertEquals(this.by, by);
            assertEquals(this.ch, ch);
            assertEquals(this.in, in);
            assertEquals(this.lo, lo);
            assertEquals(this.fl, fl, 0);
            assertEquals(this.db, db, 0);
        }
    }

    @Test
    public void shouldBeAbleToInstantiateClassWithPrimitiveParametersInConstructor() {
        // The loop is to assert that the method can be called several times, and also measure performance.
        for (int i = 0; i < 10; i++) {
            ClassWithPrimitiveParametersInConstructor o = Null.proxy(
                    ClassWithPrimitiveParametersInConstructor.class).build(getFactory());
            assertNotNull(o);
        }
    }

    @Override
    protected ProxyFactory createProxyFactory() {
        return new CglibProxyFactory();
    }
}
