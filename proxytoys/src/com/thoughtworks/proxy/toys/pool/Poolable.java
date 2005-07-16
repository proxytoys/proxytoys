/*
 * (c) 2004-2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.pool;

/**
 * An interface automatically implemented by the proxy instances returned from an {@link Pool}. It is not necessary to
 * implement this interface in a custom class. Cast the instance to this interface if you want to release the instance
 * explicitly.
 * 
 * @author J&ouml;rg Schaible
 */
public interface Poolable {
    /**
     * Return the managed instance of this proxy directly to its pool.
     */
    void returnInstanceToPool();
}
