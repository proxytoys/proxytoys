/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 25-Jul-2005 by Jörg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import static com.thoughtworks.proxy.toys.failover.Failover.failoverable;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;


/**
 * @author J&ouml;rg Schaible
 */
public class FailoverToyExample {

    public static void packageOverviewExample1() {
        Format[] formats = new Format[]{
                NumberFormat.getInstance(), DateFormat.getDateInstance(), new MessageFormat("{1}, {0}"),};
        Format format = failoverable(Format.class, null, null).build( new CglibProxyFactory());
        System.out.println("Format a date: " + format.format(new Date()));
        System.out.println("Format a message: " + format.format(new String[]{"John", "Doe"}));
        System.out.println("Format a number: " + format.format(new Integer(42)));
    }

    public static void packageOverviewExample2() {
        DataInput[] dataInputs = new DataInput[]{
                new DataInputStream(new ByteArrayInputStream(new byte[]{0, 'A', 0, 'n', 0, ' '})),
                new DataInputStream(new ByteArrayInputStream(new byte[]{
                        0, 'e', 0, 'x', 0, 'a', 0, 'm', 0, 'p', 0, 'l', 0, 'e'})),};
        DataInput dataInput = failoverable(
                DataInput.class, null, null).build(new StandardProxyFactory());
        StringBuffer buffer = new StringBuffer();
        try {
            while (buffer.append(dataInput.readChar()) != null)
                ;
        } catch (IOException e) {
        }
        System.out.println("Read: " + buffer.toString());
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running Failover Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
        System.out.println();
        System.out.println("Example 2 of Package Overview:");
        packageOverviewExample2();
    }
}
