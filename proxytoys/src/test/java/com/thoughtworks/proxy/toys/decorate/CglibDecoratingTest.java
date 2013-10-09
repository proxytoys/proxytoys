/*
 * (c) 2013 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 10-Oct-2013 by Joerg Schaible
 */
package com.thoughtworks.proxy.toys.decorate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.thoughtworks.proxy.factory.CglibProxyFactory;

/**
 * @author Gr&eacute;gory Joseph
 * @author J&ouml;rg Schaible
 */
public class CglibDecoratingTest {

    @Test
    public void canDeferDecorationUntilAfterProxyInstantiation() throws Exception {
        final Decorator<WithCallsInConstructor> nullDecorator = new Decorator<WithCallsInConstructor>() {};

        final WithCallsInConstructor obj = new WithCallsInConstructor();
        obj.setS("custom");
        assertEquals("sanity check", "custom", obj.getS());
        final WithCallsInConstructor withCallsInConstructor = Decorating.proxy(WithCallsInConstructor.class).with(obj).visiting(nullDecorator).build(new CglibProxyFactory(true));
        assertEquals("This won't be expected by most users, but it is \"correct\" nonetheless", "default", withCallsInConstructor.getS());
        assertEquals("And this was passed on to the underlying object too", "default", obj.getS());

        final WithCallsInConstructor obj2 = new WithCallsInConstructor();
        obj2.setS("custom");
        assertEquals("sanity check", "custom", obj2.getS());
        final WithCallsInConstructor withoutCallsInConstructor = Decorating.proxy(WithCallsInConstructor.class).with(obj2).visiting(nullDecorator).build(new CglibProxyFactory(false));
        assertEquals("And this is what most users would expect - setting interceptDuringConstruction to false", "custom", withoutCallsInConstructor.getS());
        assertEquals("And this was not passed on to the underlying object either", "custom", obj2.getS());
    }

    public class WithCallsInConstructor {
        private String s;

        public WithCallsInConstructor() {
            setS("default");
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }
}
