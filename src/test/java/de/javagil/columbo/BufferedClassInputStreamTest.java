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
package de.javagil.columbo;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

	private BufferedClassInputStream isFromJar, isFromDir, isFromDirInner, isFromDirInnerAnon, isFromDirAnon;

	@Before
	public final void init() throws ClassNotFoundException {
		isFromJar = new BufferedClassInputStream(Test.class.getName());
		isFromDir = new BufferedClassInputStream(BufferedClassInputStreamTest.class.getName());
		isFromDirInner = new BufferedClassInputStream(BufferedClassInputStreamTest.SomeInnerClass.class.getName());
		isFromDirInnerAnon = new BufferedClassInputStream(BufferedClassInputStreamTest.SomeInnerClass.getAnonClass().getName());
		isFromDirAnon = new BufferedClassInputStream(BufferedClassInputStreamTest.getAnonClass().getName());
	}

	@After
	public final void cleanup() throws IOException {
		if (isFromJar != null) {
			isFromJar.close();
		}
		if (isFromDir != null) {
			isFromDir.close();
		}
		if (isFromDirInner != null) {
			isFromDirInner.close();
		}
		// TODO close the others and refactor
	}

	@Test
	public final void determineSourceTest() {
		assertThat(isFromJar.getResourceURL().toExternalForm()).contains("junit-").endsWith("!/org/junit/Test.class");
		assertThat(isFromDir.getResourceURL().toExternalForm()).
				endsWith("/target/test-classes/de/javagil/columbo/BufferedClassInputStreamTest.class");
		assertThat(isFromDirInner.getResourceURL().toExternalForm()).
				endsWith("/target/test-classes/de/javagil/columbo/BufferedClassInputStreamTest$SomeInnerClass.class");
		assertThat(isFromDirInnerAnon.getResourceURL().toExternalForm()).
				endsWith("/target/test-classes/de/javagil/columbo/BufferedClassInputStreamTest$SomeInnerClass$1.class");
		assertThat(isFromDirAnon.getResourceURL().toExternalForm()).
				endsWith("/target/test-classes/de/javagil/columbo/BufferedClassInputStreamTest$1.class");
	}
	
	private static Object instOfAnon = new Object() {
		public String toString() { return "Anon[" + super.toString() + "]"; }
	};
	public static Class<?> getAnonClass() {			
		return instOfAnon.getClass();
	}
}
