package com.thoughtworks.proxy.factory;

import com.thoughtworks.proxy.Invoker;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public interface InvokerReference {
    Invoker getInvoker();
}