package com.thoughtworks.proxy.toys.delegate;

import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.2 $
 */
public class SimpleReference implements ObjectReference, Serializable {
    private Object instance;

    public SimpleReference(Object delegate) {
        set(delegate);
    }

    public Object get() {
        return instance;
    }

    public void set(Object item) {
        this.instance = item;
    }
}
