/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17-May-2004
 */
package com.thoughtworks.proxy.toys.delegate;

/**
 * Exception thrown if a delegation from the proxy to the delegated object fails.
 *
 * @author Dan North
 * @since 0.1
 */
public class DelegationException extends RuntimeException {

    private static final long serialVersionUID = -8777676199121620549L;
    private final Object delegate;

    /**
     * Construct a DelegationException.
     *
     * @param message  the meaningful message.
     * @param cause    a causing {@link Throwable}
     * @param delegate the delegated object
     * @since 0.1
     */
    public DelegationException(final String message, final Throwable cause, final Object delegate) {
        super(message, cause);
        this.delegate = delegate;
    }

    /**
     * Returns the delegated object.
     *
     * @return the delegated object
     * @since 0.1
     */
    public Object getDelegate() {
        return delegate;
    }
}
