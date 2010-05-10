/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26-Jul-2005
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.multicast.Multicast;
import com.thoughtworks.proxy.toys.multicast.Multicasting;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * @author J&ouml;rg Schaible
 */
public class MulticastToyExample {

    public static void packageOverviewExample1() {
        ArrayList<String> arrayList = new ArrayList<String>();
        LinkedList<String> linkedList = new LinkedList<String>();
        @SuppressWarnings("unchecked")
        List<String> listCombined = List.class.cast(Multicasting.proxy(arrayList, linkedList).build());
        if (listCombined.add("Hello")) {
            System.out.println("List 1: " + arrayList.toString());
            System.out.println("List 2: " + linkedList.toString());
        }
    }

    public static void packageOverviewExample2() {
        try {
            List<Integer> list1 = new ArrayList<Integer>();
            list1.add(5);
            list1.add(100);
            List<Integer> list2 = new LinkedList<Integer>();
            list2.add(3);
            @SuppressWarnings("unchecked")
            List<Integer> listCombined = List.class.cast(Multicasting.proxy(list1, list2).build());
            Multicast values = Multicast.class.cast(listCombined.get(0));
            System.out.println("Sum of the first integers: "
                    + values.multicastTargets(Integer.class, "intValue", null).toString());
        } catch (NoSuchMethodException e) {
            // Integer.class has a intValue method
        }
    }

    public static void packageOverviewExample3() {
        File workingDir = new File(".");
        List<String> files = Arrays.asList(workingDir.list());
        File multicast = Multicasting.proxy(File.class, List.class)
                .with(workingDir, files)
                .build(new CglibProxyFactory());
        System.out.println("Current working directory: " + multicast.getAbsolutePath());
        System.out.println("Files in working directory: " + List.class.cast(multicast).size());
    }

    public static void packageOverviewExample4() {
        try {
            Method method = String.class.getMethod("length");
            Multicast multicast = Multicasting.proxy("ProxyToys", "is", "great").build();
            System.out.println("Total number of characters: " + multicast.multicastTargets(method, null));
            String[] strings = multicast.getTargetsInArray(String.class);
            for (int i = 0; i < strings.length; i++) {
                System.out.println("String[" + i + "]: " + strings[i]);
            }
        } catch (NoSuchMethodException e) {
            // String.class has a length method
        }
    }

    public static void packageOverviewExample5() {
        List<String> list = new ArrayList<String>();
        Set<String> set = new HashSet<String>();
        list.add("ProxyToys");
        set.add(null);
        @SuppressWarnings("unchecked")
        Collection<String> collection = Collection.class.cast(Multicasting.proxy(list, set).build());
        Iterator<String> iter = collection.iterator();
        String value = iter.next();
        System.out.println("Element gained from the iterator: " + value);
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running Multicasting Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
        System.out.println();
        System.out.println("Example 2 of Package Overview:");
        packageOverviewExample2();
        System.out.println();
        System.out.println("Example 3 of Package Overview:");
        packageOverviewExample3();
        System.out.println();
        System.out.println("Example 4 of Package Overview:");
        packageOverviewExample4();
        System.out.println();
        System.out.println("Example 5 of Package Overview:");
        packageOverviewExample5();
    }
}
