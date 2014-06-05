/** License based on "The MIT License (MIT)":

	Copyright (c) 2014, "Michael Hönnig" <michael.hoennig@javagil.de>
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
	
	This license becomes void immediately in case of the licensee opening any 
	law suit against the licensor concerning patent infringement issues. 
*/
package de.javagil.columbo;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * JUnit test for class {@link Util}.
 * 
 * @author michael.hoennig@javagil.de
 */
public class UtilTest {

	@Test
	public final void isProperUtilityClass() throws Exception {
		Constructor<?>[] ctors = Util.class.getDeclaredConstructors();
		assertEquals("only a single constructor allowed", 1, ctors.length);
		assertEquals("constructor must not have parameters", 0, ctors[0].getParameterTypes().length);
		assertTrue("constructor must be private", Modifier.isPrivate(ctors[0].getModifiers()));
		assertEquals("must not have a superclass ", java.lang.Object.class, Util.class.getSuperclass());
		for (Method method: Util.class.getDeclaredMethods()) {
			assertTrue(method.getName() + " must be static", Modifier.isStatic(method.getModifiers()));
		}
		ctors[0].setAccessible(true);
		ctors[0].newInstance();
	}
	
	@Test
	public final void areEqual() {
		Integer obj0 = null;
		Integer obj0a = null;
		Integer obj1 = 10111;
		Integer obj1a = 10111;
		Integer obj2 = 42914;
		
		assertTrue(Util.areEqual(obj0, obj0));
		assertTrue(Util.areEqual(obj0, obj0a));
		assertTrue(Util.areEqual(obj1, obj1a));
		assertTrue(Util.areEqual(obj1, obj1));
		assertFalse(Util.areEqual(obj1, obj0));
		assertFalse(Util.areEqual(obj0, obj1));
		assertFalse(Util.areEqual(obj1, obj2));
	}
	
	@Test
	public final void withDefault() {
		assertEquals("orig", Util.withDefault("orig", "default"));
		assertEquals("default", Util.withDefault("", "default"));
		assertEquals("default", Util.withDefault(null, "default"));
	}
}
