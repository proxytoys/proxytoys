package com.thoughtworks.proxy.toys.hotswap;

/**
 * Interface implemented by all proxy instances created by {@link HotSwappingInvoker}.
 * 
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public interface Swappable {
    /**
     * Swaps the subject behind the proxy with a new instance.
     * 
     * @param newSubject the new subject the proxy will delegate to.
     * @return the old subject
     * @since 0.1
     */
    Object hotswap(Object newSubject);
}
