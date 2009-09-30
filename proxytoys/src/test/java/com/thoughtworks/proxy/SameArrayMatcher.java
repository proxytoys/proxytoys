package com.thoughtworks.proxy;

import org.mockito.ArgumentMatcher;

import java.util.Arrays;

public class SameArrayMatcher extends ArgumentMatcher<Object[]> {
    private Object[] a;

    public SameArrayMatcher(Object[] a) {
        this.a = a;
    }

    public boolean matches(Object o) {
        Object[] b = (Object[]) o;
        return Arrays.equals(a, b);
    }
}
