package com.thoughtworks.proxy.toys.failover;

import com.thoughtworks.proxy.ProxyFactory;

/**
 * Creates a proxy handling failover. Delegates to one object as long as there
 * is no exception, fails over to the next when an exception occurs.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class Failover {
   public static Object object(Class type, ProxyFactory proxyFactory, Object[] delegates, Class exceptionClass) {
        return new FailoverInvoker(type, proxyFactory, delegates, exceptionClass).proxy();
    }
}