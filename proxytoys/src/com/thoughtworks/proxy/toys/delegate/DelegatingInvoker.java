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
    /** delegate must implement the method's interface */
    public static final boolean EXACT_METHOD = true;
    /** delegate must have method with matching signature - not necessarily the same */
    public static final boolean SAME_SIGNATURE_METHOD = false;
    
    protected final ProxyFactory proxyFactory;
    protected final ObjectReference delegateReference;
    private final boolean staticTyping;

	public DelegatingInvoker(ProxyFactory proxyFactory, ObjectReference delegateReference, boolean staticTyping) {
        this.proxyFactory = proxyFactory;
        this.delegateReference = delegateReference;
        this.staticTyping = staticTyping;
	}

	public DelegatingInvoker(final Object delegate) {
        this(new StandardProxyFactory(), new SimpleReference(delegate), SAME_SIGNATURE_METHOD);
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		final Object result;
        
        // equals(...) and hashCode()
        if (method.equals(ClassHierarchyIntrospector.equals)) {
            // TODO this whole section is really ugly and needs cleaning up
			Object arg = args[0];
            while (arg != null && proxyFactory.isProxyClass(arg.getClass())) {
				arg = proxyFactory.getInvoker(arg);
                if (arg instanceof DelegatingInvoker) {
                    arg = ((DelegatingInvoker)arg).delegate();
                }
            }
            if (delegate() == null) {
                result = arg == null ? Boolean.TRUE : Boolean.FALSE;
            } else {
                result = delegate().equals(arg) ? Boolean.TRUE : Boolean.FALSE;
            }
		} else if (method.equals(ClassHierarchyIntrospector.hashCode)) {
			result = new Integer(hashCode());
            
        // null delegate
		} else if (delegate() == null) {
            result = null;
            
        // regular method call
        } else {
			result = invokeOnDelegate(getMethodToInvoke(method), args);
		}
		return result;
	}

	protected Object delegate() {
		return delegateReference.get();
	}

	private Method getMethodToInvoke(Method method) {
	    if(staticTyping) {
	        return method;
	    } else {
	        try {
				return delegate().getClass().getMethod(method.getName(), method.getParameterTypes());
			} catch (Exception e) {
                throw new DelegationException("Problem invoking " + method, e, delegate());
			}
	    }
	}

	protected Object invokeOnDelegate(Method method, Object[] args) throws Throwable {
	    Object delegate = delegate();
	    try {
		    return method.invoke(delegate, args);
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new DelegationException("Problem invoking " + method, e, delegate);
        }
    }

    protected Method getDelegateMethod(String methodName, Class[] parameterTypes) {
        try {
            return delegate().getClass().getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new DelegationException("Unable to find method " + methodName, e, delegate());
        }
    }
}
