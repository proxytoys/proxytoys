/*
 * Created on 04-May-2004
 *
 * (c) 2003-2005 ThoughtWorks
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.kit.PrivateInvoker;


// TODO: use the AOP alliance API: Mixin.object(Object, JoinPoint, PointCut[] cuts)

/**
 * Invoker implementation for the decorating proxy. The implementation may decorate an object or another {@link Invoker}.
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class DecoratingInvoker<T> implements Invoker {
    private static final long serialVersionUID = 8293471912861497447L;
    private Invoker decorated;
    private Decorator decorator;

    /**
     * Construct a DecoratingInvoker decorating another Invoker.
     *
     * @param decorated the decorated {@link Invoker}.
     * @param decorator the decorating instance.

     */
    public DecoratingInvoker(final Invoker decorated, final Decorator decorator) {
        this.decorated = decorated;
        this.decorator = decorator;
    }

    /**
     * Construct a DecoratingInvoker decorating another object.
     *
     * @param delegate  the decorated object.
     * @param decorator the decorating instance.

     */
    public DecoratingInvoker(final Object delegate, final Decorator decorator) {
        this(new PrivateInvoker(delegate), decorator);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        @SuppressWarnings("unchecked")
        final T typedProxy = (T)proxy;
        final Object[] decoratedArgs = decorator.beforeMethodStarts(typedProxy, method, args);
        try {
            final Object result = decorated.invoke(proxy, method, decoratedArgs);
            return decorator.decorateResult(typedProxy, method, decoratedArgs, result);
        } catch (InvocationTargetException e) {
            throw decorator.decorateTargetException(typedProxy, method, decoratedArgs, e.getTargetException());
        } catch (Exception e) {
            throw decorator.decorateInvocationException(typedProxy, method, decoratedArgs, e);
        }
    }
}
