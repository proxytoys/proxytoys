/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11-May-2004
 */
package com.thoughtworks.proxy.toys.hotswap;

/**
 * Interface implemented by all proxy instances created by {@link HotSwappingInvoker}.
 *
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public interface Swappable {
    /**
     * Swaps the subject behind the proxy with a new instance.
     *
     * @param newSubject the new subject the proxy will delegate to.
     * @return the old subject
     * @since 0.1
     */
    Object hotswap(Object newSubject);
}
