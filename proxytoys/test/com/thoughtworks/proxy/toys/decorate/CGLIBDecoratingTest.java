package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CGLIBProxyFactory;
import com.thoughtworks.proxy.toys.nullobject.Null;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CGLIBDecoratingTest extends DecoratingTestCase {

    public static class ClassWithPrimitiveParametersInConstructor {
        private boolean bo;
        private byte by;
        private char ch;
        private int in;
        private long lo;
        private float fl;
        private double db;

        public ClassWithPrimitiveParametersInConstructor(boolean bo, byte by, char ch, int in, long lo, float fl, double db) {
            assertEquals(this.bo, bo);
            assertEquals(this.by, by);
            assertEquals(this.ch, ch);
            assertEquals(this.in, in);
            assertEquals(this.lo, lo);
            assertEquals(this.fl, fl, 0);
            assertEquals(this.db, db, 0);
        }
    }

    public void testShouldBeAbleToInstantiateClassWithPrimitiveParametersInConstructor() {
        // The loop is to assert that the method can be called several times, and also measure performance.
        for (int i = 0; i < 10; i++) {
            ClassWithPrimitiveParametersInConstructor o = (ClassWithPrimitiveParametersInConstructor) Null.object(ClassWithPrimitiveParametersInConstructor.class, proxyFactory);
            assertNotNull(o);
        }
    }

    protected ProxyFactory createProxyFactory() {
        return new CGLIBProxyFactory();
    }
}
