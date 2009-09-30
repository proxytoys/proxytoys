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
 * Simple implmenentation for an {@link ObjectReference}.
 *
 * @author Aslak Helles&oslash;y
 * @since 0.2, 0.1 in package com.thoughtworks.proxy.toy.delegate
 */
public class SimpleReference implements ObjectReference, Serializable {
    private static final long serialVersionUID = 1L;
    private Object instance;

    /**
     * Construct a SimpleReference.
     *
     * @param reference The referenced object.
     * @since 0.2, 0.1 in package com.thoughtworks.proxy.toy.delegate
     */
    public SimpleReference(final Object reference) {
        set(reference);
    }

    public Object get() {
        return instance;
    }

    public void set(final Object item) {
        this.instance = item;
    }
}
