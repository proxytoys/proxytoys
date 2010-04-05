/*
 * (c) 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 19-03-2010.
 */
package com.thoughtworks.proxy.toys.privilege;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.SimpleReference;

// TODO: Javadoc
public class Privileging<T>
{
    private Class<T> type;
    private Object delegate;
    private ActionExecutor executor;

    /**
     * Creates a factory for proxy instances that allow a privileged execution of the methods of an object.
     *
     * @param type     the type of the proxy when it is finally created.
     * @return a factory that will proxy instances of the supplied type.
     */
    public static <T> PrivilegingWith<T> proxy(Class<T> type) {
        return new PrivilegingWith<T>(new Privileging<T>(type));
    }

    private Privileging(Class<T> type) {
        this.type = type;
    }
    
    public static class PrivilegingWith<T> {
        private Privileging<T> delegating;

        private PrivilegingWith(Privileging<T> delegating) {
            this.delegating = delegating;
        }

        /**
         * With this delegate.
         *
         * @param delegate the object the proxy delegates to.
         * @return the factory that will route calls to the supplied delegate.
         */
        public PrivilegingExecutedByOrBuild<T> with(Object delegate) {
            delegating.delegate = delegate;
            return new PrivilegingExecutedByOrBuild<T>(delegating);
        }
    }
    
    public static class PrivilegingExecutedByOrBuild<T> extends PrivilegingBuild<T>{
        private PrivilegingExecutedByOrBuild(Privileging<T> privileging) {
            super(privileging);
        }

        /**
         * Executed with this action executor.
         *
         * @param executor the executor that runs the privileged actions.
         * @return the factory that will route calls to the supplied delegate.
         */
        public PrivilegingBuild<T> executedBy(ActionExecutor executor) {
            privileging.executor = executor;
            return new PrivilegingBuild<T>(privileging);
        }
    }
    
    public static class PrivilegingBuild<T> {
        protected Privileging<T> privileging;

        private PrivilegingBuild(Privileging<T> privileging) {
            this.privileging = privileging;
        }

        /**
         * Creating a privileging proxy for an object using the {@link StandardProxyFactory}.
         *
         * @return the created proxy implementing the <tt>type</tt>
         */
        public T build() {
            return build(new StandardProxyFactory());
        }

        /**
         * Creating a privileging proxy for an object using a special {@link ProxyFactory}.
         *
         * @param factory the {@link ProxyFactory} to use.
         * @return the created proxy implementing the <tt>type</tt>
         */
        public T build(ProxyFactory factory) {
            return factory.<T>createProxy(new PrivilegingInvoker<Object>(factory,
                    new SimpleReference<Object>(privileging.delegate), privileging.executor), privileging.type);
        }
    }
}
