/*
 * Created on 17-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.delegate;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class DelegationException extends RuntimeException {

	private final Throwable cause;
	private final Object delegate;

	public DelegationException(String message, Throwable cause, Object delegate) {
        super(message);
        this.cause = cause;
		this.delegate = delegate;
	}
    
    public Throwable getCause() {
        return cause;
    }
    
    public Object getDelegate() {
        return delegate;
    }
}
