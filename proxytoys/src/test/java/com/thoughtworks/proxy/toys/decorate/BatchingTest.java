package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.toys.batching.Batching;
import com.thoughtworks.proxy.toys.batching.Batching.BatchingWith;
import com.thoughtworks.proxy.toys.batching.BatchingInvoker;
import com.thoughtworks.proxy.toys.batching.Unique;
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
        Barking bark(String what, @Unique Integer volume);
    }

    public static interface Barking {
        String what();
    }

    @Test
    public void batches_invocation_and_hotswaps_result() throws Throwable {
        Dog dog = mock(Dog.class);
        when(dog.bark("Woof", 1)).thenReturn(WOOF);

        BatchingWith<Dog> proxy = Batching.proxy(Dog.class);
        BatchingInvoker batcher = proxy.batcher();
        Dog batchingDog = proxy.with(dog).build();

        Barking bark = batchingDog.bark("Woof", 1);
        verify(dog, never()).bark("Woof", 1);
        assertEquals("", bark.what());

        batcher.flush();
        verify(dog).bark("Woof", 1);
        assertEquals("Woof", bark.what());
    }

    @Test
    public void overwrites_unique_invocations() throws Throwable {
        Dog dog = mock(Dog.class);
        when(dog.bark("Woof", 1)).thenReturn(WOOF);

        BatchingWith<Dog> proxy = Batching.proxy(Dog.class);
        BatchingInvoker batcher = proxy.batcher();
        Dog batchingDog = proxy.with(dog).build();

        batchingDog.bark("Woof", 1);
        batchingDog.bark("Woof", 2);
        batchingDog.bark("Oink", 1);

        verify(dog, never()).bark("Woof", 1);
        verify(dog, never()).bark("Woof", 2);
        verify(dog, never()).bark("Oink", 1);

        batcher.flush();

        verify(dog, never()).bark("Woof", 1);
        verify(dog).bark("Woof", 2);
        verify(dog).bark("Oink", 1);
    }
}
