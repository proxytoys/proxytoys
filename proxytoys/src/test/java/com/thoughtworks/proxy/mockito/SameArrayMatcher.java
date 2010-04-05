/*
 * (c) 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30-Sep-2009
 */
package com.thoughtworks.proxy.mockito;

import java.util.Arrays;

import org.mockito.ArgumentMatcher;


/**
 * @author Tianshuo Deng
 */
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
