package com.thoughtworks.proxy.toys.pool;

/**
 * The mode of serialization for pools.
 */
public enum SerializationMode {

    /**
     * <code>FORCE</code> is the value for serialization of the pool with or without serializable
     * objects. If the objects cannot be serialized, the pool is empty after serialization and must be populated again.
     */
    FORCE,

    /**
     * <code>STANDARD</code> is the value for the standard serialization of the pool with its objects.
     * If the objects cannot be serialized, a {@link java.io.NotSerializableException} is thrown.
     */
    STANDARD,

    /**
     * <code>NONE</code> is the value for serialization of the pool without the objects. The pool is
     * empty after serialization and must be populated again.
     */
    NONE

}
