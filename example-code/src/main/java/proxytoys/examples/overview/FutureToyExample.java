/*
 * Copyright (C) 2009 Thoughtworks Ltd
 * Created on 13-Oct-2009 by Paul Hammant
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import static com.thoughtworks.proxy.toys.future.Future.typedFuture;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.proxy.factory.CglibProxyFactory;


/**
 * @author J&ouml;rg Schaible
 */
public class FutureToyExample {

    public static void packageOverviewExample1() throws InterruptedException {
        List slowList = new SlowArrayList();

        List fasterList = typedFuture(List.class).with(slowList)
                          .build(new CglibProxyFactory());
        System.out.println("Items in list: " + fasterList.size());
        Thread.sleep(100);
        System.out.println("Items in list: " + fasterList.size());
    }


    public static void main(String[] args) throws InterruptedException {
        System.out.println();
        System.out.println();
        System.out.println("Running Future Toy Example");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
    }

    private static class SlowArrayList extends ArrayList {
        @Override
        public boolean add(final Object o) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    SlowArrayList.super.add(o);
                }
            });

            return true;
        }
    }
}