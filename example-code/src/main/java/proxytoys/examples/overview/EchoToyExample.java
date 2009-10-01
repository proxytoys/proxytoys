/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 21-Jul-2005 by Jörg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import static com.thoughtworks.proxy.toys.echo.Echoing.echoable;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;


/**
 * @author J&ouml;rg Schaible
 */
public class EchoToyExample {

    public static void packageOverviewExample1() {
        Map map = echoable(Map.class)
                            .with(new HashMap())
                            .to(new PrintWriter(System.err))
                            .build(new CglibProxyFactory());
        map.put("Date", new Date());
        map.put("File", new File("."));
        try {
            Iterator iter = map.keySet().iterator();
            while (true) {
                String key = (String) iter.next();
                Object value = map.get(key);
                if (value instanceof Date) {
                    Date date = (Date) value;
                    date.setTime(4711);
                } else if (value instanceof File) {
                    File file = (File) value;
                    if (file.exists()) {
                        file.renameTo(new File(".."));
                    }
                }
            }
        } catch (NoSuchElementException e) {
            // No further element
        }
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running Echo Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
    }
}
