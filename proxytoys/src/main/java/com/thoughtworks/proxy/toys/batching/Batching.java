package com.thoughtworks.proxy.toys.batching;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.toys.delegate.Delegating;

public class Batching<T> extends Delegating<T> {
    private Batching(Class<T> type) {
        super(type);
    }

    public static <T> BatchingWith<T> proxy(Class<T> type) {
        return new BatchingWith<T>(new Batching<T>(type));
    }

    public static class BatchingWith<T> extends DelegatingWith<T> {
        private final BatchingInvoker bathingInvoker;

        private BatchingWith(Delegating<T> delegating) {
            super(delegating);
            this.bathingInvoker = new BatchingInvoker();
        }

        @Override
        protected BatchingBuild<T> build() {
            return new BatchingBuild<T>(delegating, bathingInvoker);
        }

        public BatchingInvoker batcher() {
            return bathingInvoker;
        }
    }

    public static class BatchingBuild<T> extends DelegatingBuild<T> {
        private final BatchingInvoker batchingInvoker;

        public BatchingBuild(Delegating<T> delegating, BatchingInvoker batchingInvoker) {
            super(delegating);
            this.batchingInvoker = batchingInvoker;
        }

        @Override
        protected Invoker invoker(ProxyFactory factory, ObjectReference<Object> reference) {
            batchingInvoker.delegate = super.invoker(factory, reference);
            batchingInvoker.factory = factory;
            return batchingInvoker;
        }
    }
}
