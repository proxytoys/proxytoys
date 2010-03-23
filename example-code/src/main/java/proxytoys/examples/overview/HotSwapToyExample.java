/*
 * Copyright (C) 2005 Joerg Schaible
 * Created on 26-Jul-2005 by Joerg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;
import com.thoughtworks.proxy.toys.hotswap.Swappable;


/**
 * @author J&ouml;rg Schaible
 */
public class HotSwapToyExample {

    public static void packageOverviewExample1() {
        ByteArrayOutputStream outStreamOdd = new ByteArrayOutputStream();
        ByteArrayOutputStream outStreamEven = new ByteArrayOutputStream();
        OutputStream out = HotSwapping.proxy(OutputStream.class)
                              .with(null)
                              .build(new CglibProxyFactory());
        PrintWriter writer = new PrintWriter(out);
        for (int i = 0; i < 10; ++i) {
            Swappable swappable = Swappable.class.cast(out);
            if (i % 2 > 0) {
                swappable.hotswap(outStreamEven);
            } else {
                swappable.hotswap(outStreamOdd);
            }
            writer.println("Line " + (i + 1));
            writer.flush();
        }
        System.out.println();
        System.out.println("Odd lines output:");
        System.out.println(outStreamOdd.toString());
        System.out.println("Even lines output:");
        System.out.println(outStreamEven.toString());
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running HotSwap Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
    }
}
