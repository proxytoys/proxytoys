/*
 * (c) 2003-2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17-May-2004
 */
package com.thoughtworks.proxy.toys.hotswap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.delegate.DelegationMode;


/**
 * @author Dan North
 * @author Aslak Helles&oslash;y
 */
public class CglibHotSwappingTest extends AbstractProxyTest {
    @Override
    protected ProxyFactory createProxyFactory() {
        return new CglibProxyFactory();
    }

    public static class Yin {
        private final Yang yang;

        public Yin(Yang yang) {
            this.yang = yang;
        }

        public Yang getYang() {
            return yang;
        }
    }

    public static class Yang {
        private final Yin yin;

        public Yang(Yin yin) {
            this.yin = yin;
        }

        public Yin getYin() {
            return yin;
        }
    }

    @Test
    public void shouldMakeMutualDependenciesPossible() {
        Yin yin = HotSwapping.proxy(Yin.class).with(null).build(getFactory());
        Yang yang = new Yang(yin);
        Swappable.class.cast(yin).hotswap(new Yin(yang));

        // isn't this wicked?
        assertSame(yin, yang.getYin());
        assertSame(yang, yin.getYang());
    }

    /**
     * @author Aaron Knauf
     */
    @Test
    public void shouldProxyConcreteClass() {
        Class<?>[] proxyTypes = new Class[]{ArrayList.class, List.class, Cloneable.class, Serializable.class};
        Object proxy = HotSwapping.proxy(List.class).with(proxyTypes).mode(DelegationMode.DIRECT).build(getFactory());
        assertNotNull(proxy);
    }
}