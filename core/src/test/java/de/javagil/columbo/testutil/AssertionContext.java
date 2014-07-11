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
package de.javagil.columbo.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Wraps code to expect an AssertionError. 
 * Unfortunately expecting an AssertionError can't be handled using @Rule ExpectedException.
 * 
 * @author michael.hoennig@javagil.de
 */
public abstract class AssertionContext {

	protected abstract void when() throws Exception;

	public final void thenExpectAssertionError(final String expectedMessage) {
		try {
			when();
		} catch (AssertionError exc) {
			assertEquals(expectedMessage, exc.getMessage());
			return;
		} catch (Exception exc) {
			fail("expected AssertionError(" + expectedMessage + ") but got " + exc);
		}
		fail("expected AssertionError not thrown");
	}
}