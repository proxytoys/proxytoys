/*
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.autopool;

/**
 * An interface automatically implemented by the proxy instances returned
 * from an {@link AutoPool}.  It is not necessary to implement this interface
 * in a custom class. Cast the instance to this interface if you want to release
 * the instance explicitly.
 *
 * @author J&ouml;rg Schaible
 */
public interface AutoPoolable {
    /**
     * Return the managed instance of this proxy directly to its pool.
     */
    void returnInstanceToPool();
}
