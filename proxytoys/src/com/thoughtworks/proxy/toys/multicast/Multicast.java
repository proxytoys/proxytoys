/*
 * Created on 28-Jul-2005
 *
 * (c) 2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.multicast;

import java.lang.reflect.Method;


/**
 * Interface that is implemented by all multicasting proxies. Cast the proxy to access the proxied elements again or to
 * call a method on them independent of the type of the proxy.
 * 
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public interface Multicast {

    /**
     * Multicast a matching method call, that is not available with the types implemented by the proxy.
     * <p>
     * Use this possibility to operate on objects, that can typically not be proxied e.g. if the class type of the
     * target object is final like it is for a lot of basic classes of <tt>java.lang</tt>. The result of the call
     * follow the normal rules for multicast invocations.
     * </p>
     * <p>
     * Note that the implementation of this function must search the best fitting method. It is much more efficient to
     * call the overloaded version of this function with the appropriate method object.
     * </p>
     * 
     * @param type the type that has a method with the given name and matching arguments
     * @param method the method name
     * @param args the arguments of the invocation
     * @return the result of the multicast
     * @throws NoSuchMethodException if the type has no matching method
     * @throws SecurityException if the security maneger prevents the method access by reflection
     * @since 0.2
     */
    Object multicastTargets(Class type, String method, Object[] args) throws NoSuchMethodException;

    /**
     * Multicast a direct method call, that is not available with the types implemented by the proxy.
     * <p>
     * Use this possibility to operate on objects, that can typically not be proxied e.g. if the class type of the
     * target object is final like it is for a lot of basic classes of <tt>java.lang</tt>. The result of the call
     * follow the normal rules for multicast invocations.
     * </p>
     * 
     * @param method the method to call
     * @param args the arguments of the invocation
     * @return the result of the multicast
     * @since 0.2
     */
    Object multicastTargets(Method method, Object[] args);

    /**
     * Access the targets of the proxy in a typed array.
     * 
     * @param type the type of an array element
     * @return the typed array of proxy targets
     * @since 0.2
     */
    Object getTargetsInArray(Class type);

    /**
     * Access the targets of the proxy in an array.
     * 
     * @return the array of proxy targets
     * @since 0.2
     */
    Object[] getTargetsInArray();
}
