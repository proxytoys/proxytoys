/*
 * Created on 21-Mar-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxytoys;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
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
        
        foo.doVoidMethod();
        
        assertEquals(null, foo.getObject());
	}
    
    public void testShouldReturnNullObjectIfMethodReturnsAnInterface() throws Exception {
		// execute
        Foo foo = (Foo)Null.object(Foo.class);
        
        // verify
        assertNotNull(foo.getFoo());
        assertNotNull(foo.getFoo().getFoo());
	}
    
    public void testShouldCreateObjectWhichIsIdentifiableAsNullObject() throws Exception {
		Foo foo = (Foo) Null.object(Foo.class);
        assertTrue(Null.isNullObject(foo));
        assertFalse(Null.isNullObject(new Object()));
	}
}
