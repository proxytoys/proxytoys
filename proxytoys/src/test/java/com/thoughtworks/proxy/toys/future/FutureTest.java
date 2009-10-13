/*
 *
 * (c) 2003-2009 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.future;

import com.thoughtworks.proxy.ProxyTestCase;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class FutureTest extends ProxyTestCase {
    public static interface Service {
        List getList();

        void storeMessage();
    }

    public static class SlowService implements Service {
        private final CountDownLatch latch;

        public SlowService(CountDownLatch latch) {
            this.latch = latch;
        }

        public List getList() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
            return Collections.singletonList("yo");
        }

        public void storeMessage() {

        }
    }

    @Test
    public void testShouldReturnNullObjectAsIntermediateResultAndSwapWhenMethodCompletes() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Service slowService = new SlowService(latch);
        Service fastService = (Service) Future.object(slowService, getFactory());

        List stuff = fastService.getList();
        assertTrue(stuff.isEmpty());
        // let slowService.getStuff() proceed!
        latch.countDown();
        Thread.sleep(100);
        assertEquals("yo", stuff.get(0));
    }

    @Test
    public void testShouldHandleVoidMethods() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Service slowService = new SlowService(latch);
        Service fastService = (Service) Future.object(slowService, getFactory());

        fastService.storeMessage();
    }
}