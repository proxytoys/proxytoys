package com.thoughtworks.proxy.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.nullobject.Null;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.2 $
 */
public class CglibProxyFactory extends AbstractProxyFactory {
    private ProxyFactory standardProxyFactory = new StandardProxyFactory();
    // Keeps track of what is currently being created - to avoid infinite recursion
    private List creating = new ArrayList();

    class CGLIBInvocationHandlerAdapter extends CoincidentalInvocationHandlerAdapter implements InvocationHandler {
        public CGLIBInvocationHandlerAdapter(Invoker invoker) {
            super(invoker);
        }
    }

    private static final Map boxedClasses = new HashMap();

    static {
        boxedClasses.put(Boolean.TYPE, Boolean.class);
        boxedClasses.put(Integer.TYPE, Integer.class);
        boxedClasses.put(Byte.TYPE, Byte.class);
        boxedClasses.put(Short.TYPE, Short.class);
        boxedClasses.put(Long.TYPE, Long.class);
        boxedClasses.put(Double.TYPE, Double.class);
        boxedClasses.put(Character.TYPE, Character.class);
        boxedClasses.put(Float.TYPE, Float.class);
    }

    public Object createProxy(Class[] types, final Invoker invoker) {
        Class clazz = getSingleClass(types);
        if (clazz == null) {
            // slightly faster
            return standardProxyFactory.createProxy(types, invoker);
        }
        Class[] interfaces =  getInterfaces(types);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setInterfaces(interfaces);
        enhancer.setCallback(new CGLIBInvocationHandlerAdapter(invoker));
        try {
            return enhancer.create();
        } catch (CodeGenerationException e) { // cglib 2.0
        } catch (IllegalArgumentException e) { // cglib 2.0.2
        } catch (NoSuchMethodError e) {
        }
        return createWithConstructor(clazz, enhancer);
    }

    private Class[] getInterfaces(Class[] types) {
        List interfaces = new ArrayList(Arrays.asList(types));
        for (Iterator iterator = interfaces.iterator(); iterator.hasNext();) {
            Class clazz = (Class) iterator.next();
            if(!clazz.isInterface()) {
                iterator.remove();
            }
        }
        interfaces.add(InvokerReference.class);
        return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    private Class getSingleClass(Class[] types) {
        for (int i = 0; i < types.length; i++) {
            Class type = types[i];
            if(!type.isInterface()) {
                return type;
            }
        }
        return null;
    }

    private Object createWithConstructor(Class type, Enhancer enhancer) {
        Constructor constructor = getConstructor(type);
        Class[] params = constructor.getParameterTypes();
        Object[] args = new Object[params.length];
        for (int i = 0; i < args.length; i++) {
            if(!creating.contains(params[i])) {
                creating.add(params[i]);
                args[i] = Null.object(params[i], this);
                creating.remove(params[i]);
            } else {
                args[i] = null;
            }
        }
        Object result = enhancer.create(params, args);
        return result;
    }

    private Constructor getConstructor(Class type) {
        Constructor constructor = null;
        try {
            constructor = type.getConstructor(null);
        } catch (NoSuchMethodException e) {
            constructor = type.getConstructors()[0];

        }
        return constructor;
    }

    public boolean canProxy(Class type) {
        int modifiers = type.getModifiers();
        return !Modifier.isFinal(modifiers);
    }

    public boolean isProxyClass(Class type) {
        return Factory.class.isAssignableFrom(type) || (!type.equals(Object.class) && Proxy.isProxyClass(type)) || standardProxyFactory.isProxyClass(type);
    }
}