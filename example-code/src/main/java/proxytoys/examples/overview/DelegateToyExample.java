/*
 * Copyright (C) 2005 Joerg Schaible
 * Created on 29-Jun-2005 by Joerg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import static com.thoughtworks.proxy.toys.delegate.Delegating.delegatable;
import static com.thoughtworks.proxy.toys.delegate.DelegationMode.DIRECT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.thoughtworks.proxy.kit.ObjectReference;

/**
 * @author J&ouml;rg Schaible
 */
public class DelegateToyExample {

    public static void packageOverviewExample1() {
        ThreadLocal<Boolean> threadLocal = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return Boolean.TRUE;
            }
        };
        @SuppressWarnings("unchecked")
        ObjectReference<Boolean> ref = delegatable(ObjectReference.class)
                                 .with(threadLocal)
                                 .build();
        System.out.println("This ObjectReference has an initial value of <" + ref.get() + ">");
    }

    public static DataInput createDataInputFromFile(File f) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        raf.writeBytes("Content");
        raf.seek(0);
        return delegatable(DataInput.class)
                    .with(raf)
                    .mode(DIRECT)
                    .build();
    }

    public static void packageOverviewExample2() throws IOException {
        File tempFile = File.createTempFile("Toy", null);
        try {
            DataInput dataInput = createDataInputFromFile(tempFile);
            String line = dataInput.readLine();
            System.out.println();
            System.out.println("Data read: " + line);
            DataOutput dataOutput = (DataOutput) dataInput;
            dataOutput.writeBytes("This line will not be reached!");
        } catch (ClassCastException e) {
            System.out.println("Could not cast to DataOutput: " + e.getMessage());
        } finally {
            tempFile.delete();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println();
        System.out.println("Running Delegate Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
        System.out.println();
        System.out.println("Example 2 of Package Overview:");
        packageOverviewExample2();
    }
}
