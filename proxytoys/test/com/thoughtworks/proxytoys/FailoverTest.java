package com.thoughtworks.proxytoys;

import com.thoughtworks.proxy.toys.failover.Failover;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public abstract class FailoverTest extends ProxyTestCase {

    public static interface FailsOnNthCall {
        void doIt();
        int dunIt();
    }

    public static class FailsOnNthCallImpl implements FailsOnNthCall {
        private final int failsOn;
        private int dunItCount = 0;

        public FailsOnNthCallImpl(int failsOn) {
            this.failsOn = failsOn;
        }

        public void doIt() {
            if (dunItCount == failsOn) {
                throw new RuntimeException();
            }
            dunItCount++;
        }

        public int dunIt() {
            return dunItCount;
        }

    }

    public void testShouldFailoverToNextOnSpecialException() {
        FailsOnNthCall first = new FailsOnNthCallImpl(1);
        FailsOnNthCall second = new FailsOnNthCallImpl(1);
        FailsOnNthCall failover = (FailsOnNthCall) Failover.object(FailsOnNthCall.class, proxyFactory, new Object[]{first, second}, RuntimeException.class);
        assertEquals(0, first.dunIt());
        assertEquals(0, second.dunIt());
        failover.doIt();
        assertEquals(1, first.dunIt());
        assertEquals(0, second.dunIt());
        failover.doIt();
        assertEquals(1, first.dunIt());
        assertEquals(1, second.dunIt());
    }
}