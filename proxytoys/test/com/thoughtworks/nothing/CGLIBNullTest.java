package com.thoughtworks.nothing;

import com.thoughtworks.proxytoys.CGLIBProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class CGLIBNullTest extends NullTest {
    protected void setUp() throws Exception {
        super.setUp();
        Null.proxyFactory = new CGLIBProxyFactory();
    }

    public static class ClassWithPrimitiveParametersInConstructor {
        private boolean bo;
        private byte by;
        private int in;
        private long lo;
        private float fl;
        private double db;

        public ClassWithPrimitiveParametersInConstructor(boolean bo, byte by, char ch, int in, long lo, float fl, double db) {
            assertEquals(this.bo, bo);
            assertEquals(this.by, by);
            assertEquals(this.in, in);
            assertEquals(this.lo, lo);
            assertEquals(this.fl, fl, 0);
            assertEquals(this.db, db, 0);
        }
    }

    public void testShouldBeAbleToInstantiateClassWithPrimitiveParametersInConstructor() {
        ClassWithPrimitiveParametersInConstructor o = (ClassWithPrimitiveParametersInConstructor) Null.object(ClassWithPrimitiveParametersInConstructor.class);
    }
}