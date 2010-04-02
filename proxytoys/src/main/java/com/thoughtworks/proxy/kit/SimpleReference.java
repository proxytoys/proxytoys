/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17-May-2004
 */
package com.thoughtworks.proxy.kit;

import java.io.Serializable;

/**
 * Simple implementation for an {@link ObjectReference}.
 *
 * @author Aslak Helles&oslash;y
 * @since 0.2
 */
public class SimpleReference<T> implements ObjectReference<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private T instance;

    /**
     * Construct a SimpleReference.
     *
     * @param reference The referenced object.
     * @since 0.2
     */
    public SimpleReference(final T reference) {
        set(reference);
    }

    public T get() {
        return instance;
    }

    public void set(final T item) {
        this.instance = item;
    }
}
