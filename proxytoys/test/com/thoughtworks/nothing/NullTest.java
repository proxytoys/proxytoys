/*
 * Created on 21-Mar-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.nothing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

    public interface SomePrimitives {
        boolean getBoolean();
        byte getByte();
        char getChar();
        int getInt();
        long getLong();
        float getFloat();
        double getDouble();
    }
    
    public void testShouldReturnDefaultValuesForPrimitives() throws Exception {
        // execute
		SomePrimitives nullObject = (SomePrimitives) Null.object(SomePrimitives.class);
        
        // verify
        assertNotNull(nullObject);
        assertEquals(false, nullObject.getBoolean());
        assertEquals(0, nullObject.getByte());
        assertEquals(0, nullObject.getChar());
        assertEquals(0, nullObject.getInt());
        assertEquals(0, nullObject.getLong());
        assertEquals(0.0, nullObject.getFloat(), 0.0);
        assertEquals(0.0, nullObject.getDouble(), 0.0);
	}

    public interface SomeArrays {
        int[] getIntArray();
        Object[] getObjectArray();
    }
    
    public void testShouldReturnEmptyArrayForArrayMethods() throws Exception {
        // execute
		SomeArrays nullObject = (SomeArrays) Null.object(SomeArrays.class);
        
        // verify
        assertEquals(0, nullObject.getIntArray().length);
        assertEquals(0, nullObject.getObjectArray().length);
	}
    
    public interface SomeCollections {
        Map getMap();
        List getList();
        Set getSet();
        SortedSet getSortedSet();
        SortedMap getSortedMap();
    }
    
    public void testShouldReturnStandardNullObjectsForCollections() throws Exception {
        // execute
		SomeCollections nullObject = (SomeCollections) Null.object(SomeCollections.class);
        
        // verify
        assertSame(Collections.EMPTY_MAP, nullObject.getMap());
        assertSame(Collections.EMPTY_LIST, nullObject.getList());
        assertSame(Collections.EMPTY_SET, nullObject.getSet());
        assertSame(Null.NULL_SORTED_SET, nullObject.getSortedSet());
        assertSame(Null.NULL_SORTED_MAP, nullObject.getSortedMap());
	}
    
    public interface InterfaceWithVoidMethod {
        void doVoidMethod();
    }
    
    public void testShouldExecuteVoidMethods() throws Exception {
        // execute
		InterfaceWithVoidMethod nullObject =
            (InterfaceWithVoidMethod) Null.object(InterfaceWithVoidMethod.class);
        
        // verify
        nullObject.doVoidMethod();
	}
    
    public interface InterfaceWithObjectMethod {
        Object getObject();
    }
    
    public void testShouldReturnNullForMethodsThatReturnAnObject() throws Exception {
        // execute
		InterfaceWithObjectMethod nullObject =
            (InterfaceWithObjectMethod) Null.object(InterfaceWithObjectMethod.class);
        
        // verify
        assertEquals(null, nullObject.getObject());
	}
    
    public void testShouldRecogniseNullCollectionsAsNullObjects() throws Exception {
		assertTrue("Map", Null.isNullObject(Null.object(Map.class)));
		assertTrue("Set", Null.isNullObject(Null.object(Set.class)));
		assertTrue("List", Null.isNullObject(Null.object(List.class)));
		assertTrue("Collection", Null.isNullObject(Null.object(Collection.class)));
		assertTrue("SortedSet", Null.isNullObject(Null.object(SortedSet.class)));
		assertTrue("SortedMap", Null.isNullObject(Null.object(SortedMap.class)));
	}
    
    private void assertImmutable(Collection nullObject) {
        try {
            nullObject.add(new Object());
            fail("add");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            nullObject.addAll(Collections.singleton(new Object()));
            fail("addAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
    
    public void testShouldThrowUnsupportedOperationWhenAddingToNullCollection() throws Exception {
        assertImmutable((Collection) Null.object(Collection.class));
    }
    
    public void testShouldThrowUnsupportedOperationWhenAddingToNullSortedSet() throws Exception {
        assertImmutable((SortedSet) Null.object(SortedSet.class));
    }

	public void testShouldThrowUnsupportedOperationWhenAddingToNullSortedMap() throws Exception {
        SortedMap nullObject = (SortedMap) Null.object(SortedMap.class);
        
        try {
            nullObject.put("should fail", "really");
            fail("put");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            nullObject.putAll(Collections.singletonMap("should fail", "really"));
            fail("putAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
    
    public void testShouldReturnImmutableNullCollectionsForNullSortedMap() throws Exception {
        SortedMap nullObject = (SortedMap) Null.object(SortedMap.class);

        assertSame(Collections.EMPTY_SET, nullObject.keySet());
        assertSame(Collections.EMPTY_LIST, nullObject.values());
        assertSame(Collections.EMPTY_SET, nullObject.entrySet());
	}

    public interface InterfaceWithInterfaceMethod {
        InterfaceWithInterfaceMethod getSubInterface();
    }
    
    public void testShouldReturnNullObjectIfMethodReturnsAnInterface() throws Exception {
		// execute
        InterfaceWithInterfaceMethod nullObject =
            (InterfaceWithInterfaceMethod)Null.object(InterfaceWithInterfaceMethod.class);
        
        // verify
        assertNotNull(nullObject.getSubInterface());
        assertNotNull(nullObject.getSubInterface().getSubInterface());
	}
    
    public interface SimpleInterface {
    }
    
    public void testShouldCreateObjectWhichIsIdentifiableAsNullObject() throws Exception {
        // execute
		SimpleInterface nullObject = (SimpleInterface) Null.object(SimpleInterface.class);

        // verify
        assertTrue(Null.isNullObject(nullObject));
		assertFalse(Null.isNullObject(new Object()));
	}
    
    public void testShouldReturnNonNullStringForToStringMethod() throws Exception {
        // execute
        SimpleInterface nullObject = (SimpleInterface)Null.object(SimpleInterface.class);
        
        // verify
		assertNotNull(nullObject.toString());
	}
    
    public void testShouldCompareEqualToAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(Null.object(SimpleInterface.class), Null.object(SimpleInterface.class));
	}

    private static void assertNotEquals(Object o1, Object o2) {
        assertFalse("Should be different", o1.equals(o2));
    }
    
    public interface Type1 {}
    public interface Type2 {}
    
    public void testShouldCompareUnequalToAnotherNullObjectOfDifferentType() throws Exception {
        assertNotEquals(Null.object(Type1.class), Null.object(Type2.class));
        assertNotEquals(Null.object(Type2.class), Null.object(Type1.class));
	}
    
    public void testShouldHaveSameHashcodeAsAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(Null.object(Type1.class).hashCode(), Null.object(Type1.class).hashCode());
        assertEquals(Null.object(Type2.class).hashCode(), Null.object(Type2.class).hashCode());
	}

    public interface ShouldSerialize extends Serializable {
    }
    
    public void testShouldBeSerializableIfInterfaceIsSerializable() throws Exception {
        // setup
		Object nullObject = Null.object(ShouldSerialize.class);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        
        // execute
        out.writeObject(nullObject);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        Object result = in.readObject();
        
        // verify
        assertNotNull("not null", result);
        assertTrue("is Null Object", Null.isNullObject(result));
        assertTrue("is correct type", result instanceof ShouldSerialize);
	}
    
    public interface ShouldNotSerialize {
    }
    
    public void testShouldNotBeSerializableIfInterfaceIsNotSerializable() throws Exception {
        // setup
		ShouldNotSerialize nullObject = (ShouldNotSerialize) Null.object(ShouldNotSerialize.class);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        
        // execute
        try {
			out.writeObject(nullObject);
            fail("Should throw exception");
		} catch (IOException expected) {
		}

        try {
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(bytes.toByteArray()));
			in.readObject();
            fail("Should throw exception");
		} catch (IOException expected) {
		}
	}
}
