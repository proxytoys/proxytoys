package com.thoughtworks.proxy.toys.hotswap;

/**
 * Interface implemented by all proxy instances created by {@link HotSwappingInvoker}.
 *
 * @author Aslak Helles&oslash;y
 */
public interface Swappable<T> {
    /**
     * Swaps the subject behind the proxy with a new instance.
     *
     * @param newSubject the new subject the proxy will delegate to.
     * @return the old subject

     */
    T hotswap(Object newSubject);
}
