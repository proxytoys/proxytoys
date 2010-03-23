/*
 * Copyright (C) 2005 Joerg Schaible
 * Created on 29-Jun-2005 by Joerg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.thoughtworks.proxy.toys.dispatch.Dispatching;

/**
 * @author J&ouml;rg Schaible
 */
public class DispatcherToyExample {

    public static void packageOverviewExample1() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ArrayList<String> list = new ArrayList<String>();
        TreeMap<Object, Object> map = new TreeMap<Object, Object>();

        Checksum checksum = Dispatching.proxy(Checksum.class, DataInput.class, DataOutput.class, List.class)
                .with(list, new CRC32(), new DataInputStream(new ByteArrayInputStream("Hello Proxy!".getBytes())),
                        new DataOutputStream(outputStream), map)
                .build();

        DataOutput.class.cast(checksum).writeBytes("Chameleon");
        @SuppressWarnings("unchecked")
        List<String> stringLlist = List.class.cast(checksum);
        stringLlist.add("Frankenstein");

        System.out.println("Read a line: " + DataInput.class.cast(checksum).readLine());
        System.out.println("Once written: " + outputStream.toString());
        System.out.println("List contains: " + list.toString());
        System.out.println("Current CRC32 value: " + checksum.getValue());
    }

    public static void packageOverviewExample2() throws IOException {
        File tempFile = File.createTempFile("Demo", null);
        try {
            RandomAccessFile file = new RandomAccessFile(tempFile, "rw");
            Object proxy = Dispatching.proxy(DataInput.class, DataOutput.class)
                    .with(file)
                    .build();

            DataOutput.class.cast(proxy).writeBytes("One matches both");
            file.seek(0);
            System.out.println("Just written: " + DataInput.class.cast(proxy).readLine());
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
