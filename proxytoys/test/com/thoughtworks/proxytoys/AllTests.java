/*
 * Created on 23-Mar-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxytoys;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.thoughtworks.proxytoys");
		//$JUnit-BEGIN$
		suite.addTestSuite(EchoProxyTest.class);
		suite.addTestSuite(DelegatingProxyTest.class);
		suite.addTestSuite(NullTest.class);
		//$JUnit-END$
		return suite;
	}
}
