package com.thoughtworks.proxy.toys.failover;

import com.thoughtworks.proxy.ProxyTestCase;

import java.io.IOException;
import java.io.Serializable;


/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class FailoverTest extends ProxyTestCase {

    public static interface FailsOnNthCall {
        void doIt();

        int dunIt();
    }

    public static class FailsOnNthCallImpl implements FailsOnNthCall, Serializable {
        private static final long serialVersionUID = 1L;
        private int failsOn;
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
        FailsOnNthCall failover = (FailsOnNthCall)Failover.object(FailsOnNthCall.class, getFactory(), new Object[]{
                first, second}, RuntimeException.class);
        assertEquals(0, first.dunIt());
        assertEquals(0, second.dunIt());
        failover.doIt();
        assertEquals(1, first.dunIt());
        assertEquals(0, second.dunIt());
        failover.doIt();
        assertEquals(1, first.dunIt());
        assertEquals(1, second.dunIt());
    }

    private void useSerializedProxy(FailsOnNthCall failoverr) {
        assertEquals(1, failoverr.dunIt());
        failoverr.doIt();
        assertEquals(1, failoverr.dunIt());
        try {
            failoverr.doIt();
            fail("Thrown " + RuntimeException.class.getName() + " expected");
        } catch (final RuntimeException e) {
        }
    }

    public void testSerializeWithJDK() throws IOException, ClassNotFoundException {
        final FailsOnNthCall failover = (FailsOnNthCall)Failover.object(
                FailsOnNthCall.class, getFactory(), new Object[]{new FailsOnNthCallImpl(1), new FailsOnNthCallImpl(1)},
                RuntimeException.class);
        failover.doIt();
        useSerializedProxy((FailsOnNthCall)serializeWithJDK(failover));
    }

    public void testSerializeWithXStream() {
        final FailsOnNthCall failover = (FailsOnNthCall)Failover.object(
                FailsOnNthCall.class, getFactory(), new Object[]{new FailsOnNthCallImpl(1), new FailsOnNthCallImpl(1)},
                RuntimeException.class);
        failover.doIt();
        useSerializedProxy((FailsOnNthCall)serializeWithXStream(failover));
    }

    public void testSerializeWithXStreamInPureReflectionMode() {
        final FailsOnNthCall failover = (FailsOnNthCall)Failover.object(
                FailsOnNthCall.class, getFactory(), new Object[]{new FailsOnNthCallImpl(1), new FailsOnNthCallImpl(1)},
                RuntimeException.class);
        failover.doIt();
        useSerializedProxy((FailsOnNthCall)serializeWithXStreamAndPureReflection(failover));
    }

}