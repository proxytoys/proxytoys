/*
 * Created on 04-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.Invoker;

/**
 * @author Dan North
 * @author Aslak Helles&oslash;y
 */
public class DecoratingInvoker implements Invoker {
    private final Invoker decorated;
    private final InvocationDecorator decorator;
    
	public DecoratingInvoker(Invoker decorated, InvocationDecorator decorator) {
        this.decorated = decorated;
        this.decorator = decorator;
	}

    public DecoratingInvoker(Object delegate, InvocationDecorator decorator) {
        this(new SimpleInvoker(delegate), decorator);
    }

    public Object invoke(Object proxy, Method method, Object[]args) throws Throwable {
        try {
            Object[] decoratedArgs = decorator.beforeMethodStarts(proxy, method, args);
            Object result = decorated.invoke(proxy, method, decoratedArgs);
            return decorator.decorateResult(result);
        } catch (InvocationTargetException e) {
            throw decorator.decorateTargetException(e.getTargetException());
        } catch (Exception e) {
            throw decorator.decorateInvocationException(e);
        }
    }
}
