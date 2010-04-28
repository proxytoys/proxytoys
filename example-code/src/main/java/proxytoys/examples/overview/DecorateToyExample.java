/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29-Jun-2005
 */
package proxytoys.examples.overview;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.decorate.Decorating;
import com.thoughtworks.proxy.toys.decorate.Decorator;


/**
 * @author J&ouml;rg Schaible
 */
public class DecorateToyExample {

	public static void packageOverviewExample1() {
        List<String> list = Arrays.asList("1", "2", "3");
        @SuppressWarnings({"serial", "unchecked"})
        Decorator<Iterator> decorator = new Decorator<Iterator>() {
            @Override
            public Object decorateResult(Iterator proxy, Method method, Object[] args, Object result) {
                if (method.getName().equals("next"))
                    return Integer.valueOf(String.class.cast(result));
                else
                    return result;
            }
        };
    	@SuppressWarnings("unchecked")
        Iterator<Integer> intIter = Decorating.proxy(Iterator.class)
        					 .with(list.iterator())
                             .visiting(decorator)
                             .build();
        while (intIter.hasNext()) {
            Integer i = intIter.next();
            System.out.println(i);
        }
    }

    public static void packageOverviewExample2() {
        File file = new File(".");
        @SuppressWarnings("serial")
        Decorator<File> decorator = new Decorator<File>() {
            @Override
            public Object[] beforeMethodStarts(File proxy, Method method, Object[] args) {
                System.out.print("Called: " + method.getName());
                return super.beforeMethodStarts(proxy, method, args);
            }
            @Override
            public Object decorateResult(File proxy, Method method, Object[] args, Object result) {
                System.out.println(" ==> " + result);
                return result;
            }
        };
        File decoratedFile = Decorating.proxy(file)
                                .visiting(decorator)
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
