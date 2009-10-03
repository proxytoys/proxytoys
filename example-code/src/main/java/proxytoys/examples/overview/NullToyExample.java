/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 30-Jul-2005 by Jörg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.nullobject.Null;
import static com.thoughtworks.proxy.toys.nullobject.Null.nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;


/**
 * @author J&ouml;rg Schaible
 */
public class NullToyExample {

    public static void packageOverviewExample1() {
        try {
            ProxyFactory factory = new CglibProxyFactory();
            File file = nullable(File.class).build(factory);
            System.out.println("Length is: " + file.length());
            System.out.println("Exists: " + file.exists());
            System.out.println("Array is empty: " + file.list().length);
            System.out.println("toURL returns null, since URL is final: " + (file.toURL() == null));
            System.out.println("Parent file is Null proxy: " + Null.isNullObject(file.getParentFile(), factory));

        } catch (MalformedURLException e) {
            // ignore
        }
    }

    public static void listExample() {
        ProxyFactory factory = new CglibProxyFactory();
        List list = nullable(List.class).build(factory);
        System.out.println("\n\nLength is: " + list.size());
        System.out.println("contains: " + list.contains("FOO"));
        List one = nullable(List.class).build(factory);
        List other = nullable(List.class).build(factory);
        System.out.println("two are ==? " + (one == other));
        System.out.println("two are .equals? " + (one.equals(other)));
        try {
        System.out.println("and you can't add to lists: " + list.add("Bar"));
        } catch (UnsupportedOperationException expected) {
            System.out.println("expected and got this one " + expected);
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
