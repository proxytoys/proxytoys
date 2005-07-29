/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 29.06.2005 by Jörg Schaible
 * See license.txt for license details
 */
package proxytoys.tutorial.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.decorate.Decorating;
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
        List list = Arrays.asList(new String[]{"1", "2", "3"});
        Iterator intIter = (Iterator)Decorating.object(Iterator.class, list.iterator(), new InvocationDecoratorSupport() {
            public Object decorateResult(Object proxy, Method method, Object[] args, Object result) {
                if (method.getName().equals("next"))
                    return Integer.valueOf((String)result);
                else
                    return result;
            }
        });
        while (intIter.hasNext()) {
            Integer i = (Integer)intIter.next();
            System.out.println(i);
        }
    }

    public static void packageOverviewExample2() {
        File file = new File(".");
        File decoratedFile = (File)Decorating.object(new Class[]{File.class}, file, new InvocationDecoratorSupport() {
            public Object[] beforeMethodStarts(Object proxy, Method method, Object[] args) {
                System.out.print("Called: " + method.getName());
                return super.beforeMethodStarts(proxy, method, args);
            }

            public Object decorateResult(Object proxy, Method method, Object[] args, Object result) {
                System.out.println(" ==> " + result);
                return result;
            }
        }, new CglibProxyFactory());
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
