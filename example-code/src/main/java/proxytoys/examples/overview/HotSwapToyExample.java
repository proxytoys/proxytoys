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
