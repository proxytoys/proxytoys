package com.thoughtworks.proxytoys;

import com.thoughtworks.nothing.Null;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;
import net.sf.cglib.core.CodeGenerationException;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.2 $
 */
public class CGLIBProxyFactory extends AbstractProxyFactory {
    private ProxyFactory standardProxyFactory = new StandardProxyFactory();

    class CGLIBInvocationHandlerAdapter implements InvocationHandler, Serializable {
        private final Invoker invocationInterceptor;

        public CGLIBInvocationHandlerAdapter(Invoker invocationInterceptor) {
            this.invocationInterceptor = invocationInterceptor;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(getInvocationInterceptor)) {
                return invocationInterceptor;
            }
            return invocationInterceptor.invoke(proxy, method, args);
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

    public Object createProxy(Class type, final Invoker invoker) {
        if (type.isInterface()) {
            // slightly faster
            return standardProxyFactory.createProxy(type, invoker);
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setInterfaces(new Class[]{InvokerReference.class});
        enhancer.setCallback(new CGLIBInvocationHandlerAdapter(invoker));
        try {
            return enhancer.create();
        } catch (CodeGenerationException e) {
            return createWithConstructor(type, enhancer);
        } catch (NoSuchMethodError e) {
            return createWithConstructor(type, enhancer);
        }
    }

    private Object createWithConstructor(Class type, Enhancer enhancer) {
        Constructor constructor = getConstructor(type);
        Class[] params = constructor.getParameterTypes();
        Object[] args = new Object[params.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = Null.object(params[i]);
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