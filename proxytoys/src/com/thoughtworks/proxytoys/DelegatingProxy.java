/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxytoys;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class DelegatingProxy {

    public static class DelegatingInvocationHandler implements InvocationHandler {
        protected final Object delegate;
        
        public DelegatingInvocationHandler(Object delegate) {
            this.delegate = delegate;
        }

        /**
         * @throws RemoteException if anything goes wrong that would
         *         cause deployment to fail (eg. missing/inaccessible method)
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return invokeOnDelegate(method.getName(), method.getParameterTypes(), args);
        }
        
        protected Object invokeOnDelegate(String methodName, Class[] parameterTypes, Object[] args) throws Throwable {
            try {
                if (delegate == null) {
                    return null;
                }
                return delegate.getClass().getMethod(methodName, parameterTypes).invoke(delegate, args);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
            catch (Exception e) {
                throw new RemoteException("Problem calling " + methodName, e);
            }
        }
    }
    
    public static Object newProxyInstance(Class type, Object delegate) {
        return Proxy.newProxyInstance(type.getClassLoader(),
                new Class[] {type},
                new DelegatingInvocationHandler(delegate));
    }
}
