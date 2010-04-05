/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07-Aug-2005
 */
package proxytoys.examples.overview;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.SimpleInvoker;


/**
 * @author J&ouml;rg Schaible
 */
public class ProxyFactoryExample {

    public static void packageOverviewExample1() {
        ProxyFactory factory = new StandardProxyFactory();
        List<String> proxy = factory.createProxy(new SimpleInvoker(new ArrayList<String>()), List.class);
        proxy.add("Hello World");
        System.out.println("Size of list: " + proxy.size());
        System.out.println("First element of list: " + proxy.get(0));
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running ProxyFactory Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
    }
}
