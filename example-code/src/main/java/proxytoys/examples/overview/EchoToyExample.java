/*
 * (c) 2005, 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 21-Jul-2005
 */
package proxytoys.examples.overview;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.echo.Echoing;


/**
 * @author J&ouml;rg Schaible
 */
public class EchoToyExample {

    public static void packageOverviewExample1() {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = Echoing.proxy(Map.class)
                            .with(new HashMap<String, Object>())
                            .to(new PrintWriter(System.err))
                            .build(new CglibProxyFactory());
        map.put("Date", new Date());
        map.put("File", new File("."));
        try {
            Iterator<String> iter = map.keySet().iterator();
            while (true) {
                String key = iter.next();
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
