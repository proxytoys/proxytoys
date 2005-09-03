/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 02-Aug-2005 by Jörg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.kit.Resetter;
import com.thoughtworks.proxy.toys.pool.Pool;
import com.thoughtworks.proxy.toys.pool.Poolable;

import java.util.zip.CRC32;
import java.util.zip.Checksum;


/**
 * @author J&ouml;rg Schaible
 */
public class PoolToyExample {

    public static void packageOverviewExample1() {
        Pool pool = new Pool(Checksum.class, new Resetter() {
            public boolean reset(final Object object) {
                ((Checksum)object).reset();
                return true;
            }
        });
        pool.add(new CRC32());
        if (true) {
            Checksum checksum = (Checksum)pool.get();
            checksum.update("JUnit".getBytes(), 0, 5);
            System.out.println("CRC32 checksum of \"JUnit\": " + checksum.getValue());
        }
        if (true) {
            Checksum checksum = (Checksum)pool.get();
            if (checksum == null) {
                System.out.println("No checksum available, force gc ...");
                System.gc();
            }
            checksum = (Checksum)pool.get();
            System.out.println("CRC32 of an resetted checksum: " + checksum.getValue());
            ((Poolable)checksum).returnInstanceToPool();
        }
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running Pool Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
    }
}
