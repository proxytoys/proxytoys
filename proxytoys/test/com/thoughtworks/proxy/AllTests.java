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

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class AllTests {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.thoughtworks.proxy");
		//$JUnit-BEGIN$
		//$JUnit-END$
		return suite;
	}
}
