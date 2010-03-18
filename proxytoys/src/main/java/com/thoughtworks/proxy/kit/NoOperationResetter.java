/*
 * Created on 24-Aug-2005
 *
 * (c) 2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.kit;

import java.io.Serializable;


/**
 * A Resetter that has no operation. This Resetter implementation will just return the object to the pool without
 * further interaction.
 *
 * @author J&ouml;rg Schaible
 */
public class NoOperationResetter<T> implements Resetter<T>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc} Returns just <code>true</code>.
     */
    public boolean reset(T object) {
        return true;
    }

}
