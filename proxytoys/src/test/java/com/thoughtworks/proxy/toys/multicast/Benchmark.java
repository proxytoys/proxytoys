package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

import java.io.Serializable;

public class Benchmark {
    public void run() {
        Impl i1 = new Impl();
        Impl i2 = new Impl();
        Impl i3 = new Impl();

        I manualTail = new HandcodedMulticastingImpl(i1, i2, i3);
        I proxyTail = Multicasting.proxy(I.class).with(i1, i2, i3).build(new StandardProxyFactory());
        I cglibTail = Multicasting.proxy(I.class).with(i1, i2, i3).build(new CglibProxyFactory());

        int iterations = 10000000;

        // warmup/JIT
        bench(i1, iterations);
        bench(manualTail, iterations);
        bench(proxyTail, iterations);
        bench(cglibTail, iterations);

        bench(i1, iterations);
        bench(manualTail, iterations);
        bench(proxyTail, iterations);
        bench(cglibTail, iterations);

        bench(i1, iterations);
        bench(manualTail, iterations);
        bench(proxyTail, iterations);
        bench(cglibTail, iterations);

        System.gc();

        System.out.println("direct:  " + bench(i1, iterations) + " nanos/call");
        System.out.println("manual:  " + bench(manualTail, iterations) + " nanos/call");
        System.out.println("proxy :  " + bench(proxyTail, iterations) + " nanos/call");
        System.out.println("cglib :  " + bench(proxyTail, iterations) + " nanos/call");
    }

    private double bench(I i, int iterations) {
        long manualStart = System.nanoTime();
        for (int n = 0; n < iterations; n++) {
            i.m();
        }
        return (System.nanoTime() - manualStart) / (double) iterations;
    }

    public static interface I {
        void m();
    }

    public static class Impl implements I, Serializable {
        public void m() {
        }
    }

    private class HandcodedMulticastingImpl implements I {
        private final I[] tails;

        public HandcodedMulticastingImpl(I... tails) {
            this.tails = tails;
        }

        public void m() {
            for (I i : tails) {
                i.m();
            }
        }
    }

    /*
    Aslak's benchmark on java-6-sun-1.6.0.24 on Ubuntu 10.04 LS

    direct:  5.9242866 nanos/call
    manual:  7.5449264 nanos/call
    proxy :  53.8074008 nanos/call
    cglib :  53.7519256 nanos/call

     */
    public static void main(String[] args) {
        new Benchmark().run();
    }
}
