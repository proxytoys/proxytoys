/*
 * (c) 2003-2004 ThoughtWorks
 * 
 * See license.txt for licence details
 */
package com.thoughtworks.proxy.toys.hotswap;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.delegate.DelegatingInvoker;
import com.thoughtworks.proxy.toys.delegate.ObjectReference;

import java.lang.reflect.Method;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
public class HotSwappingInvoker extends DelegatingInvoker {
    private static final Method hotswap;
    private static final Method checkForCycle;

    static {
        try {
            hotswap = Swappable.class.getMethod("hotswap", new Class[]{Object.class});
            checkForCycle = CycleCheck.class.getMethod("checkForCycle", null);
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    private static interface CycleCheck {
        void checkForCycle();
    }
    
    private final Class[] types;
    private transient boolean executed = false;
    
    public HotSwappingInvoker(Class[] types, ProxyFactory proxyFactory, ObjectReference delegateReference, boolean staticTyping) {
        super(proxyFactory, delegateReference, staticTyping);
        this.types = types;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (method.equals(hotswap)) {
            result = hotswap(args[0]);
        } else if (method.equals(checkForCycle)) {
            if (executed) {
                throw new IllegalStateException("Cyclic dependency");
            } else {
                if (delegate() instanceof CycleCheck) {
                    executed = true;
                    ((CycleCheck)delegate()).checkForCycle();
                    executed = false;
                }
            }
            return Void.TYPE;
        } else {
			result = super.invoke(proxy, method, args);
        }
        return result;
    }

    public Object hotswap(Object newDelegate) {
        Object result = delegateReference.get();
        delegateReference.set(newDelegate);
        if (newDelegate instanceof CycleCheck) {
            ((CycleCheck)newDelegate).checkForCycle();
        }
        return result;
    }

    public Object proxy() {
        Class[] typesWithSwappable = new Class[types.length + 2];
        System.arraycopy(types, 0, typesWithSwappable, 0, types.length);
        typesWithSwappable[types.length] = Swappable.class;
        typesWithSwappable[types.length+1] = CycleCheck.class;
        return proxyFactory.createProxy(typesWithSwappable, this);
    }
}
