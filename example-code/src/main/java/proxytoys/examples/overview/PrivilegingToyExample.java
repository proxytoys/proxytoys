/*
 * (c) 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10-Apr-2009
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.privilege.AccessControllerExecutor;
import com.thoughtworks.proxy.toys.privilege.Privileging;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;


/**
 * @author Joerg Schaible
 */
public class PrivilegingToyExample {

    public static void packageOverviewExample1() throws IOException {
        File file = Privileging.proxy(new File("src/main/java/" + PoolToyExample.class.getName().replace('.', '/') + ".java"))
                .executedBy(new AccessControllerExecutor())
                .build(new CglibProxyFactory());
        LineNumberReader reader = new LineNumberReader(new FileReader(file), 16 * 1024);
        while (reader.readLine() != null) ;
        System.out.println("Lines of code: " + reader.getLineNumber());
        reader.close();
    }


    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println();
        System.out.println("Running Privileging Toy Example");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
    }
}