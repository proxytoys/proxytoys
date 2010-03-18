package com.thoughtworks.proxy;

import java.util.Arrays;

import org.mockito.ArgumentMatcher;

public class SameArrayMatcher extends ArgumentMatcher<Object[]> {
    private Object[] a;

    public SameArrayMatcher(Object[] a) {
        this.a = a;
    }

    @Override
    public boolean matches(Object o) {
        Object[] b = (Object[]) o;
        return Arrays.equals(a, b);
    }
}
