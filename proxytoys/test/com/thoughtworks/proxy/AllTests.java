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

import com.thoughtworks.proxy.toys.hide.CgHidingTest;
import com.thoughtworks.proxy.toys.multicast.CgMulticastTest;
import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospectorTest;
import com.thoughtworks.proxy.toys.nullobject.CgNullTest;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class AllTests {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.thoughtworks.proxy");
        suite.addTest(StandardTests.suite());
        suite.addTest(CgTests.suite());
        suite.addTestSuite(CgHidingTest.class);
        suite.addTestSuite(CgMulticastTest.class);
        suite.addTestSuite(ClassHierarchyIntrospectorTest.class);
        suite.addTestSuite(CgNullTest.class);
        return suite;
	}
}
