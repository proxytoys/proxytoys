/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 30.07.2005 by Jörg Schaible
 * See license.txt for license details
 */
package proxytoys.tutorial.overview;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.nullobject.Null;

import java.io.File;
import java.net.MalformedURLException;


/**
 * @author J&ouml;rg Schaible
 */
public class NullToyExample {

    public static void packageOverviewExample1() {
        try {
            ProxyFactory factory = new CglibProxyFactory();
            File file = (File)Null.object(File.class, factory);
            System.out.println("Length is: " + file.length());
            System.out.println("Exists: " + file.exists());
            System.out.println("Array is empty: " + file.list().length);
            System.out.println("toURL returns null, since URL is final: " + (file.toURL() == null));
            System.out.println("Parent file is Null proxy: " + Null.isNullObject(file.getParentFile(), factory));
            
        } catch (MalformedURLException e) {
            // ignore
        }
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running Null Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
    }
}
