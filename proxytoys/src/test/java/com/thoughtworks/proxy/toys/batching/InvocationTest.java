package com.thoughtworks.proxy.toys.batching;

import com.thoughtworks.proxy.factory.StandardProxyFactory;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InvocationTest {
    private static final Object PROXY = new Object();

    public static interface I{
        public void a(@Unique int a, int b);
        public void b(int a, int b);
    }

    private static final Method A;
    private static final Method B;

    static {
        try {
            A = I.class.getMethod("a", int.class, int.class);
            B = I.class.getMethod("b", int.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void invocation_with_same_args_are_equal() throws Exception {
        Invocation i1 = new Invocation(new StandardProxyFactory(), PROXY, A, new Integer[]{1,2});
        Invocation i2 = new Invocation(new StandardProxyFactory(), PROXY, A, new Integer[]{1,2});
        assertEquals(i1, i2);
        assertEquals(i1.hashCode(), i2.hashCode());
    }

    @Test
    public void invocation_with_equal_unique_args_are_equal() throws Exception {
        Invocation i1 = new Invocation(new StandardProxyFactory(), PROXY, A, new Integer[]{1,2});
        Invocation i2 = new Invocation(new StandardProxyFactory(), PROXY, A, new Integer[]{1,3});
        assertEquals(i1, i2);
        assertEquals(i1.hashCode(), i2.hashCode());
    }

    @Test
    public void invocation_with_different_unique_args_are_not_equal() throws Exception {
        Invocation i1 = new Invocation(new StandardProxyFactory(), PROXY, A, new Integer[]{1,2});
        Invocation i2 = new Invocation(new StandardProxyFactory(), PROXY, A, new Integer[]{2,2});
        assertFalse(i1.equals(i2));
        assertFalse(i1.hashCode() == i2.hashCode());
    }

    @Test
    public void invocation_with_different_args_are_not_equal() throws Exception {
        Invocation i1 = new Invocation(new StandardProxyFactory(), PROXY, B, new Integer[]{1,2});
        Invocation i2 = new Invocation(new StandardProxyFactory(), PROXY, B, new Integer[]{2,2});
        assertFalse(i1.equals(i2));
        assertFalse(i1.hashCode() == i2.hashCode());
    }
}
