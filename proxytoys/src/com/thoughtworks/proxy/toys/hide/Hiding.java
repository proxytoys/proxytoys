/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.hide;

import com.thoughtworks.proxy.ProxyFactory;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class Hiding {

	/**
	 * @return a proxy that hides the implementation and implements {@link Swappable}.
	 */
	public static Object object(Class type, ProxyFactory proxyFactory, Object delegate) {
	    return new HidingInvoker(type, proxyFactory, delegate).proxy();
	}
}
