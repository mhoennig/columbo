/** License based on "The MIT License (MIT)":

	Copyright (c) 2014, "Michael Hoennig" <michael.hoennig@javagil.de>
	
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
package de.javagil.columbo.core.internal;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import de.javagil.columbo.core.internal.BufferedClassInputStream;

/**
 * Unit test for class {@link BufferedClassInputStream}
 * 
 * @author michael.hoennig@javagil.de
 *
 */
public class BufferedClassInputStreamTest {

	/**
	 * just to test the class reader
	 */
	static class SomeInnerClass {

		private static Object instOfAnon = new Object() {
			public String toString() { return "Anon[" + super.toString() + "]"; }
		};
		public static Class<?> getAnonClass() {			
			return instOfAnon.getClass();
		}
	}

	private BufferedClassInputStream inStream;

	@After
	public final void cleanup() throws IOException {
		if (inStream != null) {
			inStream.close();
		}
	}

	@Test
	public final void determineSourceForClassInJarTest() throws ClassNotFoundException {
		assertThat(urlString(classInputStreamFor(Test.class))).
				contains("junit-").endsWith("!/org/junit/Test.class");
	}
	
	@Test
	public final void determineSourceForClassInDirectoryTest() throws ClassNotFoundException {
		assertThat(urlString(classInputStreamFor(BufferedClassInputStreamTest.class))).
				endsWith("/target/test-classes/de/javagil/columbo/core/internal/BufferedClassInputStreamTest.class");
	}
	
	@Test
	public final void determineSourceForInnerClassTest() throws ClassNotFoundException {
		assertThat(urlString(classInputStreamFor(BufferedClassInputStreamTest.SomeInnerClass.class))).
				endsWith("/target/test-classes/de/javagil/columbo/core/internal/BufferedClassInputStreamTest$SomeInnerClass.class");
	}

	@Test
	public final void determineSourceForAnonymousClassTest() throws ClassNotFoundException {
		assertThat(urlString(classInputStreamFor(BufferedClassInputStreamTest.getAnonClass()))).
				endsWith("/target/test-classes/de/javagil/columbo/core/internal/BufferedClassInputStreamTest$1.class");
	}
	
	@Test
	public final void determineSourceForAnomousClassInInnerClassTest() throws ClassNotFoundException {
		assertThat(urlString(classInputStreamFor(BufferedClassInputStreamTest.SomeInnerClass.getAnonClass()))).
				endsWith("/target/test-classes/de/javagil/columbo/core/internal/BufferedClassInputStreamTest$SomeInnerClass$1.class");
	}
	
	@Test
	public final void determineSourceForMethodLocalInnerClassTest() throws ClassNotFoundException {
		assertThat(urlString(classInputStreamFor(this.getSomeMethodLocalInnerClass()))).
				endsWith("/target/test-classes/de/javagil/columbo/core/internal/BufferedClassInputStreamTest$1InnerMethodClass.class");
	}
	
	
	// ----- end of tests --- just test fixture below -------------------------------------------
	
	private BufferedClassInputStream classInputStreamFor(final Class<?> clazz) throws ClassNotFoundException {
		inStream = new BufferedClassInputStream(clazz.getName());
		return inStream;
	}

	private String urlString(final BufferedClassInputStream classInputStream) {
		return classInputStream.getResourceURL().toExternalForm();
	}
	
	private Class<?> getSomeMethodLocalInnerClass() {
		/**
		 * An inner class within a method for testing purposes. 
		 */
		class InnerMethodClass {
		}
		
		return new InnerMethodClass().getClass();
	}

	private static Object instOfAnon = new Object() {
		public String toString() { return "Anon[" + super.toString() + "]"; }
	};

	public static Class<?> getAnonClass() {			
		return instOfAnon.getClass();
	}

}
