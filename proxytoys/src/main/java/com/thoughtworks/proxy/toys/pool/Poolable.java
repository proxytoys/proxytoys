/*
 * (c) 2004, 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01-Jul-2004
 */
package com.thoughtworks.proxy.toys.pool;

/**
 * An interface automatically implemented by the proxy instances returned from an {@link Pool}. It is not necessary to
 * implement this interface in a custom class. Cast the instance to this interface if you want to release the instance
 * explicitly.
 *
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public interface Poolable {
    /**
     * Return the managed instance of this proxy directly to its pool.
     * 
     * @since 0.2
     */
    void returnInstanceToPool();
}
