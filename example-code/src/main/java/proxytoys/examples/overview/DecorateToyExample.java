/*
 * Copyright (C) 2005 Joerg Schaible
 * Created on 29-Jun-2005 by Joerg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import static com.thoughtworks.proxy.toys.decorate.Decorating.decoratable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.decorate.Decorator;


/**
 * @author J&ouml;rg Schaible
 */
public class DecorateToyExample {

    public static void packageOverviewExample1() {
        List<String> list = Arrays.asList("1", "2", "3");
        @SuppressWarnings("serial")
        Decorator decorator = new Decorator() {
            @Override
            public Object decorateResult(Object proxy, Method method, Object[] args, Object result) {
                if (method.getName().equals("next"))
                    return Integer.valueOf(String.class.cast(result));
                else
                    return result;
            }
        };
        @SuppressWarnings("unchecked")
        Iterator<Integer> intIter = decoratable(Iterator.class)
                             .with(list.iterator(), decorator)
                             .build();
        while (intIter.hasNext()) {
            Integer i = intIter.next();
            System.out.println(i);
        }
    }

    public static void packageOverviewExample2() {
        File file = new File(".");
        @SuppressWarnings("serial")
        Decorator decorator = new Decorator() {
            @Override
            public Object[] beforeMethodStarts(Object proxy, Method method, Object[] args) {
                System.out.print("Called: " + method.getName());
                return super.beforeMethodStarts(proxy, method, args);
            }
            @Override
            public Object decorateResult(Object proxy, Method method, Object[] args, Object result) {
                System.out.println(" ==> " + result);
                return result;
            }
        };
        File decoratedFile = decoratable(File.class)
                                .with(file, decorator)
                                .build(new CglibProxyFactory());
        decoratedFile.exists();
        decoratedFile.isFile();
        decoratedFile.isDirectory();
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running Decorate Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
        System.out.println();
        System.out.println("Example 2 of Package Overview:");
        packageOverviewExample2();
    }
}
