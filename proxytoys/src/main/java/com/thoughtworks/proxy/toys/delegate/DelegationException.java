/*
 * Created on 17-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.delegate;

/**
 * Exception thrown if a delegation from the proxy to the delegated object fails.
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
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
     */
    public DelegationException(final String message, final Throwable cause, final Object delegate) {
        super(message, cause);
        this.delegate = delegate;
    }

    /**
     * Returns the delegated object.
     *
     * @return the delegated object
     */
    public Object getDelegate() {
        return delegate;
    }
}
