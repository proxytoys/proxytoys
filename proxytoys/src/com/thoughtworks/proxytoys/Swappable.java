package com.thoughtworks.proxytoys;

/**
 * Interface implemented by all proxy instances created by
 * {@link DelegatingProxy}.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.4 $
 */
public interface Swappable {
    /**
     * Swaps the subject behind the proxy with a new instance.
     * @param newSubject the new subject the proxy will delegate to.
     * @return the old subject
     */
    Object hotswap(Object newSubject);
}
