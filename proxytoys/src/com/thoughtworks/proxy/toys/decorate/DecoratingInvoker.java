/*
 * Created on 04-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import java.lang.reflect.Method;

import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;

// TODO: Shouldn't this just decorate another Invoker? Nicer chaining!! AH
public class DecoratingInvoker extends DelegatingInvoker {
    private final InvocationDecorator decorator;

	public DecoratingInvoker(Object delegate, InvocationDecorator decorator) {
        super(delegate);
        this.decorator = decorator;
	}
    
    public Object invoke(Object proxy, Method method, Object[]args) throws Throwable {
        try {
            decorator.beforeMethodStarts(proxy, method, args);
            Object result = super.invoke(proxy, method, args);
            return decorator.decorateResult(result);
        } catch (Throwable t) {
            Throwable decorated = decorator.decorateException(t);
            throw decorated;
        }
    }
}
