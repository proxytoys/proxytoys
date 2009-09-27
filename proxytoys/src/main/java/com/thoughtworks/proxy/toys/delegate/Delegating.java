/*
 * Created on 04-Feb-2004
 * 
 * (c) 2003-2005 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.delegate;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.SimpleReference;
import static com.thoughtworks.proxy.toys.delegate.DelegationMode.SIGNATURE;


/**
 * Toy factory to create proxies delegating to another object.
 * <p>
 * Such a proxy is used to mask the methods of an object, that are not part of a public interface. Or it is used to make
 * an object compatible, e.g. when an object implements the methods of an interface, but does not implement the
 * interface itself.
 * </p>
 *
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @see com.thoughtworks.proxy.toys.delegate
 * @since 0.1
 */
public class Delegating<T> {

    private Class<T> type;
    private Object delegate;
    private DelegationMode delegationMode = SIGNATURE;

    private Delegating(Class<T> type, Object delegate) {
        this.type = type;
        this.delegate = delegate;
    }

    /**
     * Creates a factory for proxy instances that allow delegation.
     *
     * @param type     the type of the proxy when it is finally created.
     * @param delegate the object the proxy delegates to.
     * @return a factory that will proxy instances of the supplied type.
     */

    public static <T> Delegating<T> delegatable(Class<T> type, Object delegate) {
        return new Delegating<T>(type, delegate);
    }

    /**
     * Forces a particular delegation mode to be used.
     *
     * @param mode refer to {@link DelegationMode#DIRECT} or
     *             {@link DelegationMode#SIGNATURE} for allowed
     *             values.
     * @return the factory that will proxy instances of the supplied type.
     */
    public Delegating<T> withDelegationMode(DelegationMode mode) {
        this.delegationMode = mode;
        return this;
    }

    /**
     * Creating a delegating proxy for an object using a special {@link StandardProxyFactory}
     *
     * @return the created proxy implementing the <tt>type</tt>
     */
    public T build() {
        return build(new StandardProxyFactory());
    }

    /**
     * Creating a delegating proxy for an object using a special {@link ProxyFactory}
     *
     * @param factory the @{link ProxyFactory} to use.
     * @return the created proxy implementing the <tt>type</tt>
     */
    public T build(ProxyFactory factory) {
        return (T) factory.createProxy(new Class[]{type}, new DelegatingInvoker(
                factory, new SimpleReference(delegate), delegationMode));
    }

}
