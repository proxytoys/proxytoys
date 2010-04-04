/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14-July-2005
 */
package com.thoughtworks.proxy.toys.dispatch;

/**
 * An exception if a type cannot be dispatched.
 *
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public class DispatchingException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Class<?> type;

    /**
     * Construct a DispatchingException with the offending type.
     *
     * @param message the meaningful message
     * @param type    the type, that cannot be dispatched
     * @since 0.2
     */
    public DispatchingException(final String message, final Class<?> type) {
        super(message);
        this.type = type;
    }

    /**
     * Returns the offending type.
     *
     * @return the type
     * @since 0.2
     */
    public Object getType() {
        return type;
    }
}
