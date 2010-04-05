/*
 * (c) 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13-Oct-2009
 */
package proxytoys.examples.overview;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.future.Future;


/**
 * @author Paul Hammant
 */
public class FutureToyExample {

    //TODO: Fails
    public static void packageOverviewExample1() throws InterruptedException {
        List<?> slowList = new SlowArrayList();
        List<?> fasterList = Future.proxy(List.class).with(slowList)
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

    @SuppressWarnings("serial")
    private static class SlowArrayList extends ArrayList<Object> {
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