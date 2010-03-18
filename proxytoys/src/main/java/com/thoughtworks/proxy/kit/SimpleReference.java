/*
 * Created on 17-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.kit;

import java.io.Serializable;

/**
 * Simple implementation for an {@link ObjectReference}.
 *
 * @author Aslak Helles&oslash;y
 */
public class SimpleReference<T> implements ObjectReference<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private T instance;

    /**
     * Construct a SimpleReference.
     *
     * @param reference The referenced object.
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
