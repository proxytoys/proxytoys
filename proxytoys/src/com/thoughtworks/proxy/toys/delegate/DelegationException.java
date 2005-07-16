/*
 * Created on 17-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.delegate;

/**
 * Exception thrown if a delegation from the proxy to the delegated object fails.
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @since 0.1
 */
public class DelegationException extends RuntimeException {

    private final Throwable cause;
    private final Object delegate;

    /**
     * Construct a DelegationException.
     * 
     * @param message the meaningful message.
     * @param cause a causing {@link Throwable}
     * @param delegate the delegated object
     */
    public DelegationException(final String message, final Throwable cause, final Object delegate) {
        super(message);
        this.cause = cause;
        this.delegate = delegate;
    }

    /**
     * Retruns the causing Throwable.
     * 
     * @return the {@link Throwable}
     */

    public Throwable getCause() {
        return cause;
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
