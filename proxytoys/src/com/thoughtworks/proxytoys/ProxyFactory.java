package com.thoughtworks.proxytoys;

/**
 * Abstraction layer for proxy generation. Depending on this interface
 * instead of {@link java.lang.reflect.Proxy} directly allows to use Java's
 * standard proxy mechanism interchangeably with e.g. CGLIB.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public interface ProxyFactory {
    Object createProxy(Class type, Invoker invoker);
    boolean canProxy(Class type);
    boolean isProxyClass(Class clazz);
    Invoker getInvoker(Object proxy);
}