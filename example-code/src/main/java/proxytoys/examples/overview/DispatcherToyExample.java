/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 29-Jun-2005 by Jörg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import static com.thoughtworks.proxy.toys.dispatch.Dispatching.dispatchable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


/**
 * @author J&ouml;rg Schaible
 */
public class DispatcherToyExample {

    public static void packageOverviewExample1() {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final ArrayList list = new ArrayList();
            final TreeMap map = new TreeMap();

            final Object proxy = dispatchable(
                    Checksum.class, DataInput.class, DataOutput.class, List.class).with(
                    list, new CRC32(), new DataInputStream(new ByteArrayInputStream("Hello Proxy!".getBytes())),
                    new DataOutputStream(outputStream), map).build();

            ((DataOutput)proxy).writeBytes("Chameleon");
            ((List)proxy).add("Frankenstein");

            System.out.println("Read a line: " + ((DataInput)proxy).readLine());
            System.out.println("Once written: " + outputStream.toString());
            System.out.println("List contains: " + list.toString());
            System.out.println("Current CRC32 value: " + ((Checksum)proxy).getValue());

        } catch (IOException e) {
            // ignore
        }
    }

    public static void packageOverviewExample2() {
        try {
            final File tempFile = File.createTempFile("Demo", null);
            try {
                final RandomAccessFile file = new RandomAccessFile(tempFile, "rw");

                final Object proxy = dispatchable(DataInput.class, DataOutput.class).with(file).build();

                ((DataOutput)proxy).writeBytes("One matches both");
                file.seek(0);
                System.out.println("Just written: " + ((DataInput)proxy).readLine());

            } finally {
                tempFile.delete();
            }

        } catch (IOException e) {
            // ignore
        }
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running Dispatcher Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
        System.out.println();
        System.out.println("Example 2 of Package Overview:");
        packageOverviewExample2();
    }
}
