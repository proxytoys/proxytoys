/*
 * Created on 17-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.kit;

/**
 * A way to refer to objects that are stored in "awkward" places (for example inside a <code>HttpSession</code> or
 * {@link ThreadLocal}). This interface is typically implemented by someone integrating with an existing container.
 * 
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 * @since 0.2, 0.1 in package com.thoughtworks.proxy.toy.delegate
 */
public interface ObjectReference {
    /**
     * Retrieve an actual reference to the object. Returns null if the reference is not available or has not been populated yet.
     * 
     * @return an actual reference to the object.
     * @since 0.2, 0.1 in package com.thoughtworks.proxy.toy.delegate
     */
    Object get();

    /**
     * Assign an object to the reference.
     * 
     * @param item the object to assign to the reference. May be <code>null</code>.
     * @since 0.2, 0.1 in package com.thoughtworks.proxy.toy.delegate
     */
    void set(Object item);
}
