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

    public static <T> Delegating<T> delegate(Class<T> type, Object delegate) {
        return new Delegating<T>(type, delegate);
    }

    public Delegating<T> withDelegationMode(DelegationMode mode) {
        this.delegationMode = mode;
        return this;
    }

    public Object build() {
        return build(new StandardProxyFactory());
    }

    public Object build(ProxyFactory factory) {
        return factory.createProxy(new Class[]{type}, new DelegatingInvoker(
                factory, new SimpleReference(delegate), delegationMode));
    }

}
