/*
 * Created on 19-Aug-2009
 * 
 * (c) 2009 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.delegate;

/**
 * Indicates the preferred way to delegate to created proxies.
 */
public enum DelegationMode {
    /**
     * The delegate must directly implement the methods interface.
     */
    DIRECT {
        @Override
        public int delegationHashcode(int hashcode) {
            return ~hashcode;
        }
    },

    /**
     * The delegate must have a method with the same name and matching signature.
     */
    SIGNATURE {
        @Override
        public int delegationHashcode(int hashcode) {
            return -hashcode;
        }
    };

    public abstract int delegationHashcode(int hashcode);
}
