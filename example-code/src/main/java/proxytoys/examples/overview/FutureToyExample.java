/*
 * Copyright (C) 2005 Joerg Schaible
 * Created on 25-Jul-2005 by Joerg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import static com.thoughtworks.proxy.toys.failover.Failover.failoverable;
import static com.thoughtworks.proxy.toys.future.Future.future;
import static com.thoughtworks.proxy.toys.future.Future.typedFuture;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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