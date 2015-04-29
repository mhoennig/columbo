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
package de.javagil.columbo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.javagil.columbo.api.InspectionException;

/**
 * JUnit test for BytecodeInspectionException.
 * 
 * @author michael.hoennig@javagil.de
 */
public class InspectionExceptionTest {
	
	Referrer fakeReferrer = Mockito.mock(Referrer.class);
	
	@Before
	public final void initMocks() {
		Mockito.when(fakeReferrer.toContentString()).thenReturn("fakeReferrer");
	}

	@Test
	public final void constructorUsingMessageString() {
		InspectionException exc = new InspectionException("my message");
		assertEquals("my message", exc.getMessage());
	}

	@Test
	public final void constructorUsingException() {
		Exception originalExc = new IOException("some io exception");
		InspectionException exc = new InspectionException(originalExc);
		assertEquals(originalExc, exc.getCause());
		assertEquals("java.io.IOException: some io exception", exc.getMessage());
	}

	@Test
	public final void asRuntimeExceptionWithRuntimeExceptionReturnsSame() {
		RuntimeException origExc = new RuntimeException();
		RuntimeException rtExc = InspectionException.asRuntimeException(origExc);
		assertSame(origExc, rtExc);
	}

	@Test
	public final void asRuntimeExceptionWithBytecodeInspectionExceptionReturnsSame() {
		RuntimeException origExc = new InspectionException(new RuntimeException());
		RuntimeException rtExc = InspectionException.asRuntimeException(origExc);
		assertSame(origExc, rtExc);
	}


	@Test
	public final void asRuntimeExceptionWithCheckedExceptionReturnsBytecodeInspectionException() {
		IOException origExc = new IOException();
		RuntimeException rtExc = InspectionException.asRuntimeException(origExc);
		assertEquals(InspectionException.class, rtExc.getClass());
	}
	
	@Test
	public final void createClassNotFoundException() {
		InspectionException exception = InspectionException.createClassNotFoundException(fakeReferrer, new NoClassDefFoundError("de/javagil/columbo/test/NotExistingClass"));
		assertSame(InspectionException.class, exception.getClass());
		assertEquals("fakeReferrer uses non existing class: de/javagil/columbo/test/NotExistingClass - most likely inconsistent CLASSPATH (some class compiled against a class incompatible to what's now in CLASSPATH)", 
					exception.getMessage());
	}
	@Test
	public final void createMethodNotFoundException() {
		InspectionException exception = InspectionException.createMethodNotFoundException(fakeReferrer, java.lang.String.class, 
				"someMethod", new Class<?>[]{ java.lang.String.class, int[].class, boolean.class});
		assertSame(InspectionException.class, exception.getClass());
		assertEquals("fakeReferrer uses non existing method: java.lang.String#someMethod(java.lang.String, int[], boolean) - most likely inconsistent CLASSPATH (some class compiled against a class incompatible to what's now in CLASSPATH)", 
					exception.getMessage());
	}
	
	@Test
	public final void createConstructorNotFoundException() {
		InspectionException exception = InspectionException.createConstructorNotFoundException(fakeReferrer, java.lang.String.class, 
				new Class<?>[]{ char[].class});
		assertSame(InspectionException.class, exception.getClass());
		assertEquals("fakeReferrer uses non existing constructor: java.lang.String(char[]) - most likely inconsistent CLASSPATH (some class compiled against a class incompatible to what's now in CLASSPATH)", 
					exception.getMessage());
	}
}