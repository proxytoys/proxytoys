/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.factory;

import com.thoughtworks.proxy.ProxyFactory;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class CGLIBFactoryTest extends ProxyTestCase {
	protected ProxyFactory createProxyFactory() {
		return new CGLIBProxyFactory();
	}
}
