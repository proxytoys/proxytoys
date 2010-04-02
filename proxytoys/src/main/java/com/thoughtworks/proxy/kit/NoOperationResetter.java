/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24-Aug-2005
 */
package com.thoughtworks.proxy.kit;

import java.io.Serializable;


/**
 * A Resetter that has no operation. This Resetter implementation will just return the object to the pool without
 * further interaction.
 *
 * @author J&ouml;rg Schaible
 * @since 0.2
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
