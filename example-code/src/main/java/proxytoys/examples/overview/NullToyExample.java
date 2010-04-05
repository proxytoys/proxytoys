/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30-Jul-2005
 */
package proxytoys.examples.overview;

import java.io.File;
import java.util.List;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.nullobject.Null;


/**
 * @author J&ouml;rg Schaible
 */
public class NullToyExample {

    public static void packageOverviewExample1() {
        ProxyFactory factory = new CglibProxyFactory();
        File file = Null.proxy(File.class).build(factory);
        System.out.println("Length is: " + file.length());
        System.out.println("Exists: " + file.exists());
        System.out.println("Array is empty: " + file.list().length);
        System.out.println("toURI returns null, since URI is final: " + (file.toURI() == null));
        System.out.println("Parent file is Null proxy: " + Null.isNullObject(file.getParentFile(), factory));
    }

    public static void listExample() {
        ProxyFactory factory = new CglibProxyFactory();
        @SuppressWarnings("unchecked")
        List<String> list = Null.proxy(List.class).build(factory);
        System.out.println("\n\nLength is: " + list.size());
        System.out.println("contains: " + list.contains("FOO"));
        List<?> one = Null.proxy(List.class).build(factory);
        List<?> other = Null.proxy(List.class).build(factory);
        System.out.println("two are the same? " + (one == other));
        System.out.println("two are equal? " + (one.equals(other)));
        try {
            System.out.println("and you can't add to lists: " + list.add("Bar"));
        } catch (UnsupportedOperationException expected) {
            System.out.println("got this expected exception: " + expected);
        }
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running Null Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
        listExample();
    }
}
