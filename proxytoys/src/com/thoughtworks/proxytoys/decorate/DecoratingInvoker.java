/*
 * Created on 04-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxytoys.decorate;

import java.lang.reflect.Method;

import com.thoughtworks.proxytoys.delegate.DelegatingInvoker;


public class DecoratingInvoker extends DelegatingInvoker {
    private final InvocationDecorator interceptor;

	public DecoratingInvoker(Object delegate, InvocationDecorator interceptor) {
        super(delegate);
        this.interceptor = interceptor;
	}
    
    public Object invoke(Object proxy, Method method, Object[]args) throws Throwable {
        try {
            interceptor.beforeMethodStarts(proxy, method, args);
            Object result = super.invoke(proxy, method, args);
            return interceptor.decorateResult(result);
        } catch (Throwable t) {
            throw interceptor.decorateException(t);
        }
    }
}