/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 29-Jun-2005 by Jörg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import static com.thoughtworks.proxy.toys.decorate.Decorating.decoratable;
import com.thoughtworks.proxy.toys.decorate.InvocationDecoratorSupport;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * @author J&ouml;rg Schaible
 */
public class DecorateToyExample {

    public static void packageOverviewExample1() {
        List list = Arrays.asList("1", "2", "3");
        Iterator intIter = decoratable(
                Iterator.class).with(list.iterator(), new InvocationDecoratorSupport() {
                    public Object decorateResult(Object proxy, Method method, Object[] args, Object result) {
                        if (method.getName().equals("next"))
                            return Integer.valueOf((String)result);
                        else
                            return result;
                    }
                }).build();
        while (intIter.hasNext()) {
            Integer i = (Integer)intIter.next();
            System.out.println(i);
        }
    }

    public static void packageOverviewExample2() {
        File file = new File(".");
        File decoratedFile = decoratable(File.class).with( file, new InvocationDecoratorSupport() {
            public Object[] beforeMethodStarts(Object proxy, Method method, Object[] args) {
                System.out.print("Called: " + method.getName());
                return super.beforeMethodStarts(proxy, method, args);
            }

            public Object decorateResult(Object proxy, Method method, Object[] args, Object result) {
                System.out.println(" ==> " + result);
                return result;
            }
        }).build(new CglibProxyFactory());
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
