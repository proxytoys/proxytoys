package com.thoughtworks.proxy.toys.multicast;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Benchmark {
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

    public void run() {
        final Impl i1 = new Impl();
        final Impl i2 = new Impl();
        final Impl i3 = new Impl();

        I manualTail = new HandcodedMulticastingImpl(i1, i2, i3);
        I proxyTail = Multicasting.proxy(I.class).with(i1, i2, i3).build(new StandardProxyFactory());
        I cglibTail = Multicasting.proxy(I.class).with(i1, i2, i3).build(new CglibProxyFactory());
        I lwProxyTail = (I) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{I.class}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                method.invoke(i1);
                method.invoke(i2);
                method.invoke(i3);
                return null;
            }
        });

        int iterations = 100000000;

        // warmup/JIT
        bench(i1, iterations);
        bench(manualTail, iterations);
        bench(proxyTail, iterations);
        bench(cglibTail, iterations);
        bench(lwProxyTail, iterations);

        bench(i1, iterations);
        bench(manualTail, iterations);
        bench(proxyTail, iterations);
        bench(cglibTail, iterations);
        bench(lwProxyTail, iterations);

        bench(i1, iterations);
        bench(manualTail, iterations);
        bench(proxyTail, iterations);
        bench(cglibTail, iterations);
        bench(lwProxyTail, iterations);

        System.gc();

        System.out.println("direct :  " + bench(i1, iterations) + " nanos/call");
        System.out.println("manual :  " + bench(manualTail, iterations) + " nanos/call");
        System.out.println("proxy  :  " + bench(proxyTail, iterations) + " nanos/call");
        System.out.println("cglib  :  " + bench(proxyTail, iterations) + " nanos/call");
        System.out.println("lwproxy:  " + bench(lwProxyTail, iterations) + " nanos/call");
    }

    private double bench(I i, int iterations) {
        long manualStart = System.nanoTime();
        for (int n = 0; n < iterations; n++) {
            i.m();
        }
        return (System.nanoTime() - manualStart) / (double) iterations;
    }

    /*
    Aslak's benchmark on java-6-sun-1.6.0.24 on Ubuntu 10.04 LS

    direct :  4.83933067 nanos/call
    manual :  7.54567958 nanos/call
    proxy  :  54.65973182 nanos/call
    cglib  :  54.86338074 nanos/call
    lwproxy:  31.92595094 nanos/call

     */
    public static void main(String[] args) {
        new Benchmark().run();
    }
}
