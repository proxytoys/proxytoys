/*
 * Created on 23-Mar-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.thoughtworks.nothing.CGLIBNullTest;
import com.thoughtworks.nothing.StandardNullTest;
import com.thoughtworks.proxytoys.CGLIBFailoverTest;
import com.thoughtworks.proxytoys.CGLIBHidingTest;
import com.thoughtworks.proxytoys.CGLIBMulticastTest;
import com.thoughtworks.proxytoys.StandardFailoverTest;
import com.thoughtworks.proxytoys.StandardHidingTest;
import com.thoughtworks.proxytoys.StandardMulticastTest;
import com.thoughtworks.proxytoys.decorate.DecoratingProxyTest;
import com.thoughtworks.proxytoys.delegate.DelegatingProxyTest;
import com.thoughtworks.proxytoys.echo.EchoProxyTest;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.thoughtworks.proxytoys");
		//$JUnit-BEGIN$
        suite.addTestSuite(StandardNullTest.class);
        suite.addTestSuite(CGLIBNullTest.class);
        suite.addTestSuite(StandardHidingTest.class);
        suite.addTestSuite(CGLIBHidingTest.class);
        suite.addTestSuite(StandardMulticastTest.class);
        suite.addTestSuite(CGLIBMulticastTest.class);
        suite.addTestSuite(StandardFailoverTest.class);
        suite.addTestSuite(CGLIBFailoverTest.class);
		suite.addTestSuite(EchoProxyTest.class);
		suite.addTestSuite(DelegatingProxyTest.class);
		suite.addTestSuite(DecoratingProxyTest.class);
		//$JUnit-END$
		return suite;
	}
}
