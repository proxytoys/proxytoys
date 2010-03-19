/*
 *
 * (c) 2003-2009 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.future;

import static com.thoughtworks.proxy.toys.future.Future.future;
import static com.thoughtworks.proxy.toys.future.Future.typedFuture;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class FutureTest extends AbstractProxyTest {
    public static interface Service {
        List<String> getList();
        void methodReturnsVoid();
    }

    public static class SlowService implements Service {
        private final CountDownLatch latch;

        public SlowService(CountDownLatch latch) {
            this.latch = latch;
        }

        public List<String> getList() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
            return Collections.singletonList("yo");
        }

        public void methodReturnsVoid() {
        }
    }

    @Test
    public void shouldReturnNullObjectAsIntermediateResultAndSwapWhenMethodCompletesWithCast() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Service slowService = new SlowService(latch);
        Service fastService = future(slowService).build(getFactory());

        List<String> stuff = fastService.getList();
        assertTrue(stuff.isEmpty());
        // let slowService.getStuff() proceed!
        latch.countDown();
        Thread.sleep(100);
        assertEquals(1, stuff.size());
        assertEquals("yo", stuff.get(0));
    }

    @Test
    public void shouldReturnNullObjectAsIntermediateResultAndSwapWhenMethodCompletesWithGenerics() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Service slowService = new SlowService(latch);
        Service fastService = typedFuture(Service.class).with(slowService).build(getFactory());

        List<String> stuff = fastService.getList();
        assertTrue(stuff.isEmpty());
        // let slowService.getStuff() proceed!
        latch.countDown();
        Thread.sleep(100);
        assertEquals(1, stuff.size());
        assertEquals("yo", stuff.get(0));
    }

    @Test
    public void shouldHandleVoidMethodsWithCast() {
        CountDownLatch latch = new CountDownLatch(1);
        Service slowService = new SlowService(latch);
        Service fastService = future(slowService).build(getFactory());
        fastService.methodReturnsVoid();
    }

    @Test
    public void shouldHandleVoidMethodsWithGenerics() {
        CountDownLatch latch = new CountDownLatch(1);
        Service slowService = new SlowService(latch);
        Service fastService =  typedFuture(Service.class).with(slowService).build(getFactory());
        fastService.methodReturnsVoid();
    }
}