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
import static org.junit.Assert.assertSame;

import java.io.IOException;

import org.junit.Test;

/**
 * JUnit test for BytecodeInspectionException.
 * 
 * @author michael.hoennig@javagil.de
 */
public class InspectionExceptionTest {

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
	public final void createMethodNotFoundException() {
		InspectionException exception = InspectionException.createMethodNotFoundException(java.lang.String.class, 
				"someMethod", new Class<?>[]{ java.lang.String.class, int[].class, boolean.class});
		assertSame(InspectionException.class, exception.getClass());
		assertEquals("no method found for java.lang.String#someMethod(java.lang.String, int[], boolean)", 
					exception.getMessage());
	}
}