package com.thoughtworks.proxy.toys.hotswap;

import static com.thoughtworks.proxy.toys.delegate.DelegationMode.DIRECT;
import static com.thoughtworks.proxy.toys.hotswap.HotSwapping.hotSwappable;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;


/**
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
        Yin yin = hotSwappable(Yin.class).with(null).build(getFactory());
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
        Object proxy = hotSwappable(List.class).with(proxyTypes).mode(DIRECT).build(getFactory());
        assertNotNull(proxy);
    }
}