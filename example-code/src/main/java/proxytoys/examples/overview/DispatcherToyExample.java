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

    public static void packageOverviewExample1() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final ArrayList<String> list = new ArrayList<String>();
        final TreeMap map = new TreeMap();

        final Checksum checksum = dispatchable(Checksum.class, DataInput.class, DataOutput.class, List.class)
                .with(list, new CRC32(), new DataInputStream(new ByteArrayInputStream("Hello Proxy!".getBytes())),
                        new DataOutputStream(outputStream), map)
                .build();

        ((DataOutput) checksum).writeBytes("Chameleon");
        ((List<String>) checksum).add("Frankenstein");

        System.out.println("Read a line: " + ((DataInput) checksum).readLine());
        System.out.println("Once written: " + outputStream.toString());
        System.out.println("List contains: " + list.toString());
        System.out.println("Current CRC32 value: " + checksum.getValue());

    }

    public static void packageOverviewExample2() throws IOException {
        final File tempFile = File.createTempFile("Demo", null);
        try {
            final RandomAccessFile file = new RandomAccessFile(tempFile, "rw");

            final Object proxy = dispatchable(DataInput.class, DataOutput.class)
                    .with(file)
                    .build();

            ((DataOutput) proxy).writeBytes("One matches both");
            file.seek(0);
            System.out.println("Just written: " + ((DataInput) proxy).readLine());

        } finally {
            tempFile.delete();
        }

    }

    public static void main(String[] args) throws IOException {
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
