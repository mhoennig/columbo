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

import static de.javagil.columbo.testutil.AssertClass.assertThat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.Test;

/**
 * Unit test for class {@link BytecodeUtil}.
 * 
 * @author michael.hoennig@javagil.de
 *
 */
public class BytecodeUtilTest {
	
	@Test
	public final void utilityClassTest() throws Exception {
		assertThat(BytecodeUtil.class).isUtilityClass();
	}

	@Test
	public final void getJavaClassNameTest() {
		assertEquals("boolean", BytecodeUtil.getJavaClassName(boolean.class));
		assertEquals("int[]", BytecodeUtil.getJavaClassName(int[].class));
		assertEquals("java.lang.Long", BytecodeUtil.getJavaClassName(Long.class));
		assertEquals("java.lang.Object[]", BytecodeUtil.getJavaClassName(Object[].class));
	}
	
	@Test
	public final void getPrimitiveTypeByPrefixTest() {
		assertSame(boolean.class, BytecodeUtil.getPrimitiveTypeByPrefix('Z'));
		assertNull(BytecodeUtil.getPrimitiveTypeByPrefix('x'));
	}
	
	@Test
	public final void taggedTypeNameToClassTest() {
		assertSame(java.lang.Object.class, BytecodeUtil.taggedTypeNameToClass("Ljava/lang/Object;"));
		assertSame(java.lang.Object[].class, BytecodeUtil.taggedTypeNameToClass("[Ljava/lang/Object"));
	}
	
	@Test
	public final void typeNameToClassTest() {
		// no idea why Sun originally designed it that way, but yes, ...
		assertSame(int.class, BytecodeUtil.classNameToClass("int")); // ... Java style here ...
		assertSame(boolean[].class, BytecodeUtil.classNameToClass("[Z")); // ... internal for arrays
		
		assertSame(java.lang.Object.class, BytecodeUtil.classNameToClass("java.lang.Object"));
		assertSame(java.lang.Object[].class, BytecodeUtil.classNameToClass("java.lang.Object[]"));
	}
	

	@Test
	public final void findMethodTest() {
		Method method = BytecodeUtil.findMethod(String.class, "getChars", 
							new Class<?>[]{int.class, int.class, char[].class, int.class});
		assertEquals(String.class, method.getDeclaringClass());
		assertEquals(int.class, method.getParameterTypes()[0]);
		assertEquals(int.class, method.getParameterTypes()[1]);
		assertEquals(char[].class, method.getParameterTypes()[2]);
		assertEquals(int.class, method.getParameterTypes()[3]);
		
	}
	
	@Test
	public final void findConstructorTest() {
		Constructor<?> ctor = BytecodeUtil.findConstructor(String.class, new Class<?>[]{char[].class, int.class, int.class});
		assertEquals(String.class, ctor.getDeclaringClass());
		assertEquals(char[].class, ctor.getParameterTypes()[0]);
		assertEquals(int.class, ctor.getParameterTypes()[1]);
		assertEquals(int.class, ctor.getParameterTypes()[2]);
		
		assertNull(BytecodeUtil.findConstructor(String.class, new Class<?>[]{Test.class}));
	}
	
	@Test
	public final void getResourceClassNameTest() {
		assertEquals("/java/lang/String.class", BytecodeUtil.getResourceClassName(java.lang.String.class));
	}
	
	@Test
	public final void determineParameterTypesTest() {
		Class<?>[] paramTypes = BytecodeUtil.determineParameterTypes("([ILjava.lang.String;Z)V");
		assertEquals(3, paramTypes.length);
		assertEquals(int[].class, paramTypes[0]);
		assertEquals(String.class, paramTypes[1]);
		assertEquals(boolean.class, paramTypes[2]);
	}
	
}
