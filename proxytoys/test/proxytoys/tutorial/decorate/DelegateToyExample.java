/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 29.06.2005 by Jörg Schaible
 */
package proxytoys.tutorial.decorate;

import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.toys.delegate.Delegating;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @author J&ouml;rg Schaible
 */
public class DelegateToyExample {

    public static void packageOverviewExample1() {
        ThreadLocal threadLocal = new ThreadLocal() {
            protected Object initialValue() {
                return Boolean.TRUE;
            }
        };
        ObjectReference ref = (ObjectReference)Delegating.object(ObjectReference.class, threadLocal);
        System.out.println("This ObjectReference has an initial value of <" + ref.get() + ">");
    }

    public static DataInput createDataInputFromFile(File f) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        raf.writeBytes("Content");
        raf.seek(0);
        return (DataInput)Delegating.object(DataInput.class, raf, Delegating.STATIC_TYPING);
    }

    public static void packageOverviewExample2() {
        try {
            File tempFile = File.createTempFile("Toy", null);
            try {
                DataInput dataInput = createDataInputFromFile(tempFile);
                String line = dataInput.readLine();
                System.out.println();
                System.out.println("Data read: " + line);
                DataOutput dataOutput = (DataOutput)dataInput;
                dataOutput.writeBytes("This line will not be reached!");
            } catch (ClassCastException e) {
                System.out.println("Could not cast to DataOutput: " + e.getMessage());
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
        System.out.println("Running Delegating Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
        System.out.println();
        System.out.println("Example 2 of Package Overview:");
        packageOverviewExample2();
    }
}
