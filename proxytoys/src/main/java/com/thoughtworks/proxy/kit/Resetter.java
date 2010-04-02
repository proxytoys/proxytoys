/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02-Aug-2005
 */
package com.thoughtworks.proxy.kit;

/**
 * Interface for a resetter component. This will automatically reset the state of the pooled element, when it is
 * returned to the pool.
 *
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public interface Resetter<T> {
    /**
     * Reset the pooled object. The implementation may do anything to reset the state of the pooled element. If the
     * element is definitely exhausted, a return value of <code>false</code> prevents the element from returning into
     * the pool and the instance is garbage collected.
     *
     * @param object the object to reset
     * @return <code>true</code> if the element can be used for further tasks.
     * @since 0.2
     */
    boolean reset(T object);
}
