package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.toys.batching.Batching;
import com.thoughtworks.proxy.toys.batching.Batching.BatchingWith;
import com.thoughtworks.proxy.toys.batching.BatchingInvoker;
import com.thoughtworks.proxy.toys.delegate.Delegating;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class BatchingTest {
    private static final Barking WOOF = new Barking() {
        public String what() {
            return "Woof";
        }
    };

    public static interface Dog {
        Barking bark();
    }

    public static interface Barking {
        String what();
    }

    @Test
    public void batches_invocation_and_hotswaps_result() throws Throwable {
        Dog target = mock(Dog.class);
        when(target.bark()).thenReturn(WOOF);

        BatchingWith<Dog> proxy = Batching.proxy(Dog.class);
        BatchingInvoker batcher = proxy.batcher();
        Dog batchingDog = proxy.with(target).build();

        Barking bark = batchingDog.bark();
        verify(target, never()).bark();
        assertEquals("", bark.what());

        batcher.flush();
        verify(target).bark();
        assertEquals("Woof", bark.what());
    }
}
