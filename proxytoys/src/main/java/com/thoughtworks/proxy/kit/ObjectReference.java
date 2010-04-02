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

/**
 * A way to refer to objects that are stored in "awkward" places (for example inside a <code>HttpSession</code> or
 * {@link ThreadLocal}). This interface is typically implemented by someone integrating with an existing container.
 *
 * @author Joe Walnes
 * @since 0.2
 */
public interface ObjectReference<T> {
    /**
     * Retrieve an actual reference to the object. Returns null if the reference is not available or has not been
     * populated yet.
     *
     * @return an actual reference to the object.
     * @since 0.2
     */
    T get();

    /**
     * Assign an object to the reference.
     *
     * @param item the object to assign to the reference. May be <code>null</code>.
     * @since 0.2
     */
    void set(T item);
}
