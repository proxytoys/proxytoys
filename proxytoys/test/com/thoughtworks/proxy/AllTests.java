/*
 * Created on 14-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.thoughtworks.proxy.toys.decorate.CGLIBDecoratingTest;
import com.thoughtworks.proxy.toys.decorate.StandardDecoratingTest;
import com.thoughtworks.proxy.toys.delegate.CGLIBDelegatingTest;
import com.thoughtworks.proxy.toys.delegate.StandardDelegatingTest;
import com.thoughtworks.proxy.toys.echo.CGLIBEchoingTest;
import com.thoughtworks.proxy.toys.echo.StandardEchoingTest;
import com.thoughtworks.proxy.toys.failover.CGLIBFailoverTest;
import com.thoughtworks.proxy.toys.failover.StandardFailoverTest;
import com.thoughtworks.proxy.toys.hide.CGLIBHidingTest;
import com.thoughtworks.proxy.toys.hide.StandardHidingTest;
import com.thoughtworks.proxy.toys.multicast.CGLIBMulticastTest;
import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospectorTest;
import com.thoughtworks.proxy.toys.multicast.StandardMulticastTest;
import com.thoughtworks.proxy.toys.nullobject.CGLIBNullTest;
import com.thoughtworks.proxy.toys.nullobject.StandardNullTest;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class AllTests {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.thoughtworks.proxy");

        suite.addTestSuite(CGLIBDecoratingTest.class);
        suite.addTestSuite(StandardDecoratingTest.class);
        
        suite.addTestSuite(CGLIBDelegatingTest.class);
        suite.addTestSuite(StandardDelegatingTest.class);

        suite.addTestSuite(CGLIBEchoingTest.class);
        suite.addTestSuite(StandardEchoingTest.class);
        
        suite.addTestSuite(CGLIBFailoverTest.class);
        suite.addTestSuite(StandardFailoverTest.class);
        
        suite.addTestSuite(CGLIBHidingTest.class);
        suite.addTestSuite(StandardHidingTest.class);
        
        suite.addTestSuite(CGLIBMulticastTest.class);
        suite.addTestSuite(StandardMulticastTest.class);
        suite.addTestSuite(ClassHierarchyIntrospectorTest.class);

        suite.addTestSuite(CGLIBNullTest.class);
        suite.addTestSuite(StandardNullTest.class);
        
		return suite;
	}
}
