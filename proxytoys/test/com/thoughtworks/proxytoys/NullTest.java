/*
 * Created on 21-Mar-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxytoys;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author <a href="mailto:aslak@thoughtworks.com">Aslak Helles&oslash;y</a>
 */
public class NullTest extends TestCase {
    
    public interface Foo {
        // primitives
        boolean getBoolean();
        byte getByte();
		char getChar();
        int getInt();
        long getLong();
        float getFloat();
        double getDouble();
        
        // arrays
        int[] getIntArray();
        Object[] getObjectArray();
        
        // collections
        Map getMap();
        List getList();
        Set getSet();
        
        // void
        void doVoidMethod();
        
        // object
        Object getObject();
        
        // interface
        Foo getFoo();
    }
    
    public void testShouldCreateObjectWithNullBehaviour() throws Exception {
        // execute
		Foo foo = (Foo) Null.object(Foo.class);
        
        // verify
        assertNotNull(foo);
        assertEquals(false, foo.getBoolean());
        assertEquals(0, foo.getByte());
        assertEquals(0, foo.getChar());
        assertEquals(0, foo.getInt());
        assertEquals(0, foo.getLong());
        assertEquals(0.0, foo.getFloat(), 0.0);
        assertEquals(0.0, foo.getDouble(), 0.0);

        assertEquals(0, foo.getIntArray().length);
        assertEquals(0, foo.getObjectArray().length);

        assertSame(Collections.EMPTY_MAP, foo.getMap());
        assertSame(Collections.EMPTY_LIST, foo.getList());
        assertSame(Collections.EMPTY_SET, foo.getSet());
        
        foo.doVoidMethod();
        
        assertEquals(null, foo.getObject());
	}
    
    public void testShouldRecogniseNullCollectionsAsNullObjects() throws Exception {
		assertTrue("Map", Null.isNullObject(Null.object(Map.class)));
		assertTrue("Set", Null.isNullObject(Null.object(Set.class)));
		assertTrue("List", Null.isNullObject(Null.object(List.class)));
		assertTrue("SortedSet", Null.isNullObject(Null.object(SortedSet.class)));
		assertTrue("SortedMap", Null.isNullObject(Null.object(SortedMap.class)));
	}
    
    public void testShouldThrowUnsupportedOperationWhenMutatingNullSortedSet() throws Exception {
        SortedSet sortedSet = (SortedSet) Null.object(SortedSet.class);
        
        Object object = new Object();
        
		try {
            sortedSet.add(object);
            fail("add");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            sortedSet.addAll(Collections.singleton(object));
            fail("addAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            sortedSet.clear();
            fail("clear");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            sortedSet.remove(object);
            fail("remove");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            sortedSet.removeAll(Collections.singleton(object));
            fail("removeAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            sortedSet.retainAll(Collections.singleton(object));
            fail("retainAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
    
    public void testShouldThrowUnsupportedOperationWhenMutatingNullSortedMap() throws Exception {
        SortedMap map = (SortedMap) Null.object(SortedMap.class);
        try {
            map.put("should fail", "really");
            fail("put");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            map.putAll(Collections.singletonMap("should fail", "really"));
            fail("putAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            map.clear();
            fail("clear");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            map.remove("should fail");
            fail("remove");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        
        assertSame(Collections.EMPTY_SET, map.keySet());
        assertSame(Collections.EMPTY_LIST, map.values());
        assertSame(Collections.EMPTY_SET, map.entrySet());
    }
    
    public void testShouldReturnNullObjectIfMethodReturnsAnInterface() throws Exception {
		// execute
        Foo foo = (Foo)Null.object(Foo.class);
        
        // verify
        assertNotNull(foo.getFoo());
        assertNotNull(foo.getFoo().getFoo());
	}
    
    public void testShouldCreateObjectWhichIsIdentifiableAsNullObject() throws Exception {
        // execute
		Foo foo = (Foo) Null.object(Foo.class);

        // verify
        assertTrue(Null.isNullObject(foo));
		assertFalse(Null.isNullObject(new Object()));
	}
    
    public void testShouldReturnNonNullStringForToStringMethod() throws Exception {
        // execute
        Foo foo = (Foo)Null.object(Foo.class);
        
        // verify
		assertNotNull(foo.toString());
	}
    
    public void testShouldCompareEqualToAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(Null.object(Foo.class), Null.object(Foo.class));
	}

    private static void assertNotEquals(Object o1, Object o2) {
        assertFalse("Should be different", o1.equals(o2));
    }

    private static void assertNotEquals(int i1, int i2) {
        assertFalse("Should be different", i1 == i2);
    }
    
    public void testShouldCompareUnequalToAnotherNullObjectOfDifferentType() throws Exception {
        assertNotEquals(Null.object(Collection.class), Null.object(List.class));
        assertNotEquals(Null.object(List.class), Null.object(Collection.class));
	}
    
    public void testShouldHaveSameHashcodeAsAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(Null.object(Collection.class).hashCode(), Null.object(Collection.class).hashCode());
	}
    
    public void testShouldUsuallyHaveDifferentHashcodeFromAnotherNullObjectOfDifferentType() throws Exception {
        assertNotEquals(Null.object(Collection.class).hashCode(), Null.object(List.class).hashCode());
	}
}
