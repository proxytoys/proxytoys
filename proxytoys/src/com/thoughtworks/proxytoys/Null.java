/*
 * Created on 21-Mar-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxytoys;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Utility class for creating Null Objects
 * 
 * A null object has deterministically boring behaviour as follows:
 * <ul>
 * <li>If a method returns a <em>primitive</em> the null object returns
 * the default for that type (e.g. <tt>false</tt> for <tt>boolean</tt>).
 * 
 * <li>If a method returns an array, the null object returns an empty
 * array, which is usually nicer than just returninng <tt>null</tt>.
 * (In fact an empty array is just the null object for arrays.)
 * 
 * <li>If the method returns an interface the null object returns
 * a new null object implementing that interface (so you can recurse
 * or step through object graphs without surprises).
 * 
 * <li>Otherwise the null object just returns <tt>null</tt>.
 * </ul>
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author <a href="mailto:aslak@thoughtworks.com">Aslak Helles&oslash;y</a>
 */
public class Null {
    
	public static final SortedMap NULL_SORTED_MAP = new TreeMap() {
		public Object put(Object key, Object value) {
			throw new UnsupportedOperationException();
		}
		public void clear() {
			throw new UnsupportedOperationException();
		}
		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}
		public Set keySet() {
			return Collections.EMPTY_SET;
		}
		public Collection values() {
			return Collections.EMPTY_LIST;
		}
		public Set entrySet() {
			return Collections.EMPTY_SET;
		}
	};
            
	public static final SortedSet NULL_SORTED_SET = new TreeSet() {
		public boolean add(Object o) {
			throw new UnsupportedOperationException();
		}
		public void clear() {
			throw new UnsupportedOperationException();
		}
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
		public boolean removeAll(Collection c) {
			throw new UnsupportedOperationException();
		}
		public boolean retainAll(Collection c) {
			throw new UnsupportedOperationException();
		}
	};
            
    /**
     * Create a new Null Object:
     * <pre>
     *     Foo foo = (Foo)Null.object(Foo.class); // null object
     * 
     *     Null.isNullObject(foo); // true
     * </pre>
     * 
     */
    public static Object object(Class type) {
        final Object result;
        
        // Primitives
        if (boolean.class == type) {
            result = Boolean.FALSE;
        }
        else if (byte.class == type) {
            result = new Byte((byte) 0);
        }
        else if (char.class.equals(type)) {
            result = new Character((char) 0);
        }
        else if (int.class.equals(type)) {
            result = new Integer(0);
        }
        else if (long.class.equals(type)) {
            result = new Long(0);
        }
        else if (float.class.equals(type)) {
            result = new Float(0.0);
        }
        else if (double.class.equals(type)) {
            result = new Double(0.0);
        }
        
        // Arrays
        else if (type.isArray()) {
            result = Array.newInstance(type.getComponentType(), 0);
        }
        
        // Collections
        else if (Set.class == type) {
            result = Collections.EMPTY_SET;
        }
        else if (Map.class == type) {
            result = Collections.EMPTY_MAP;
        }
        else if (List.class == type) {
            result = Collections.EMPTY_LIST;
        }
        else if (SortedSet.class == type) {
            result = NULL_SORTED_SET;
        }
        else if (SortedMap.class == type) {
            result = NULL_SORTED_MAP;
        }
        
        // Interface
        else if (type.isInterface()) {
            result = Proxy.newProxyInstance(type.getClassLoader(),
        			new Class[] {type},
        			new NullInvocationHandler(type));
        }
        
        // Class
        else {
            result = null;
        }
        return result;
    }
    
    /**
     * Determine whether an object was created by {@link Null#object(Class)}
     */
    public static boolean isNullObject(Object object) {
        return isStandardNullObject(object) || isNullProxyObject(object);
    }

	private static boolean isStandardNullObject(Object object) {
		return object == Collections.EMPTY_LIST
        || object == Collections.EMPTY_SET
        || object == Collections.EMPTY_MAP
        || object == NULL_SORTED_SET
        || object == NULL_SORTED_MAP;
	}

	private static boolean isNullProxyObject(Object object) {
		return Proxy.isProxyClass(object.getClass())
		&& Proxy.getInvocationHandler(object) instanceof NullInvocationHandler;
	}
}
