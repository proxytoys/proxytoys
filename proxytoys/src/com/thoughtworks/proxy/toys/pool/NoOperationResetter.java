/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 24.08.2005 by Jörg Schaible
 */
package com.thoughtworks.proxy.toys.pool;

import java.io.Serializable;

/**
 * A Resetter that has no operation. This Resetter implementation will just return the object to the pool without
 * further interaction.
 * 
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public class NoOperationResetter implements Resetter, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc} Returns just <code>true</code>.
     */
    public boolean reset(Object object) {
        return true;
    }

}
