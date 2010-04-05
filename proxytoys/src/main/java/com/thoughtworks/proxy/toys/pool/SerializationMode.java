/*
 * (c) 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03-Oct-2009
 */
package com.thoughtworks.proxy.toys.pool;

/**
 * The mode of serialization for pools.
 * 
 * @author Paul Hammant
 * @since 1.0
 */
public enum SerializationMode {

    /**
     * <code>FORCE</code> is the value for serialization of the pool with or without serializable
     * objects. If the objects cannot be serialized, the pool is empty after serialization and must be populated again.
     * @since 1.0
     */
    FORCE,

    /**
     * <code>STANDARD</code> is the value for the standard serialization of the pool with its objects.
     * If the objects cannot be serialized, a {@link java.io.NotSerializableException} is thrown.
     * @since 1.0
     */
    STANDARD,

    /**
     * <code>NONE</code> is the value for serialization of the pool without the objects. The pool is
     * empty after serialization and must be populated again.
     * @since 1.0
     */
    NONE

}
