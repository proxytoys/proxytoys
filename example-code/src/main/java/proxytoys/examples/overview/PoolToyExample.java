/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 02-Aug-2005 by Jörg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.kit.Resetter;
import com.thoughtworks.proxy.toys.pool.Pool;
import static com.thoughtworks.proxy.toys.pool.Pool.poolable;
import com.thoughtworks.proxy.toys.pool.Poolable;

import java.util.zip.CRC32;
import java.util.zip.Checksum;


/**
 * @author J&ouml;rg Schaible
 */
public class PoolToyExample {

    public static void packageOverviewExample1() {
        Resetter<Checksum> resetter = new Resetter<Checksum>() {
            public boolean reset(Checksum object) {
                object.reset();
                return true;
            }
        };
        Pool<Checksum> pool = poolable(Checksum.class, resetter).withNoInstances().build(new CglibProxyFactory());
        pool.add(new CRC32());
        {
            Checksum checksum = pool.get();
            checksum.update("JUnit".getBytes(), 0, 5);
            System.out.println("CRC32 checksum of \"JUnit\": " + checksum.getValue());
        }
        {
            Checksum checksum = pool.get();
            if (checksum == null) {
                System.out.println("No checksum available, force gc ...");
                System.gc();
            }
            checksum = pool.get();
            System.out.println("CRC32 of an resetted checksum: " + checksum.getValue());
            ((Poolable) checksum).returnInstanceToPool();
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
