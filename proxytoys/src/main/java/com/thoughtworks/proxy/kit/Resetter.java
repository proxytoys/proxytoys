/*
 * Created on 02-Aug-2005
 * 
 * (c) 2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.kit;

/**
 * Interface for a resetter component. This will automatically reset the state of the pooled element, when it is
 * retunred to the pool.
 *
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public interface Resetter {
    /**
     * Reset the pooled object. The implementation may do anything to reset the state of the pooled element. If the
     * element is definately exhausted, a return value of <code>false</code> prevents the element from returning into
     * the pool and the instance is garbage collected.
     *
     * @param object the object to reset
     * @return <code>true</code> if the element can be used for further tasks.
     * @since 0.2
     */
    boolean reset(Object object);
}
