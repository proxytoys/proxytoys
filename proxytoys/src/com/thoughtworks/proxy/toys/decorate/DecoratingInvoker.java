/*
 * Created on 04-May-2004
 *
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.kit.SimpleInvoker;

// TODO: use the AOP alliance API: Mixin.object(Object, JoinPoint, PointCut[] cuts)

/**
 * Invoker implementation for the decorating proxy. The implementation may decorate an object or another {@link Invoker}.
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public class DecoratingInvoker implements Invoker {
    private final Invoker decorated;
    private final InvocationDecorator decorator;

	/**
	 * Construct a DecoratingInvoker decorating another Invoker.
	 * 
	 * @param decorated The decorated {@link Invoker}.
	 * @param decorator The decorating instance.
	 */
	public DecoratingInvoker(Invoker decorated, InvocationDecorator decorator) {
        this.decorated = decorated;
        this.decorator = decorator;
	}

    /**
     * Construct a DecoratingInvoker decorating another object.
     * 
     * @param delegate The decorated object.
     * @param decorator The decorating instance.
     */
    public DecoratingInvoker(Object delegate, InvocationDecorator decorator) {
        this(new SimpleInvoker(delegate), decorator);
    }

    public Object invoke(Object proxy, Method method, Object[]args) throws Throwable {
        Object[] decoratedArgs = decorator.beforeMethodStarts(proxy, method, args);
        try {
            Object result = decorated.invoke(proxy, method, decoratedArgs);
            return decorator.decorateResult(proxy, method, decoratedArgs, result);
        } catch (InvocationTargetException e) {
            throw decorator.decorateTargetException(proxy, method, decoratedArgs, e.getTargetException());
        } catch (Exception e) {
            throw decorator.decorateInvocationException(proxy, method, decoratedArgs, e);
        }
    }
}
