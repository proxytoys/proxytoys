/*
 * (c) 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 19-Aug-2009
 */
package com.thoughtworks.proxy.toys.delegate;

/**
 * Indicates the preferred way to delegate to created proxies.
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public enum DelegationMode {
    /**
     * The delegate must directly implement the methods interface.
     * 
     * @since 1.0
     */
    DIRECT,

    /**
     * The delegate must have a method with the same name and matching signature.
     * 
     * @since 1.0
     */
    SIGNATURE
}
