/*
 * Created on 24-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.delegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospector;

/**
 * Invoker that delegates method calls to an object.
 * 
 * <p>This forms the basis of many other proxy toys. The delegation
 * behaviour was factored out of <tt>HotSwappingInvoker</tt>.</p>
 * 
 * @see com.thoughtworks.proxy.toys.hotswap.HotSwappingInvoker
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Dan North
 */
public class DelegatingInvoker implements Invoker {
    protected final ProxyFactory proxyFactory;
    protected final ObjectReference delegateReference;
    private final boolean forceSameType;
    private boolean executing = false;

	public DelegatingInvoker(ProxyFactory proxyFactory, ObjectReference delegateReference, boolean isTypeForgiving) {
        this.proxyFactory = proxyFactory;
        this.delegateReference = delegateReference;
        this.forceSameType = isTypeForgiving;
	}

	public DelegatingInvoker(final Object delegate) {
        this(new StandardProxyFactory(), new SimpleReference(delegate), true);
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
        Object result;
        {
            if (method.equals(ClassHierarchyIntrospector.equals)) {
            Object arg = args[0];
            if (proxyFactory.isProxyClass(arg.getClass())) {
                arg = proxyFactory.getInvoker(arg);
            }
            result = new Boolean(equals(arg));
            } else if (method.equals(ClassHierarchyIntrospector.hashCode)) {
                result = new Integer(hashCode());
            } else {
                if (executing) {
                    throw new IllegalStateException("Cyclic dependency");
                }
                executing = true;
    
                result = invokeMethod(proxy, getMethodToInvoke(method), args);
            }
            executing = false;
        }
        return result;
	}

	private Method getMethodToInvoke(Method method) throws NoSuchMethodException {
	    if(forceSameType) {
	        return method;
	    } else {
	        return delegateReference.get().getClass().getMethod(method.getName(), method.getParameterTypes());
	    }
	}

	protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
	    Object delegate = delegateReference.get();
	    try {
            if (delegate == null) {
                return null;
            }
		    return method.invoke(delegate, args);
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new DelegationException("Problem invoking " + method, e, delegate);
        }
    }
}
