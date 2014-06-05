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
import static org.junit.Assert.fail;

import org.junit.Test;


/**
 * JUnit test for Referrer.
 * 
 * @author michael.hoennig@javagil.de
 */
public class ReferrerTest {

	private String givenClassName; 
	private String givenMethodName;
	private String givenMethodDesc;
	private String givenSource;
	private Integer givenLineNo;

	@Test
	public final void constructRefererWithValidArguments() {
		givenProperReferrerParameters();
		Referrer referer = whenCreatingReferrer();
		thenPropertiesOfReferrerReflectTheConstructorArguments(referer);
	}

	@Test
	public final void constructReferrerWithInvalidFileName() {
		final String aClassNameWithSlashes = "de/javagil/bytecodeinspector/BytecodeInspector";
		
		new AssertionContext() {
			void when() {
				new Referrer(aClassNameWithSlashes , "test", null, null, null);	
			}

		} .thenExpectAssertionError("not a proper Java class name");
	}
	
	@Test
	public final void toStringTest() {
		givenProperReferrerParameters();
		Referrer referer = whenCreatingReferrer();
		assertEquals(
				"de.javagil.columbo.BytecodeInspector#inspect:123", 
				referer.toContentString());
	}
	
	@Test
	public final void toContentStringTest() {
		givenProperReferrerParameters();
		Referrer referer = whenCreatingReferrer();
		assertEquals(
				"de.javagil.columbo.Referrer[de.javagil.columbo.BytecodeInspector#inspect:123]", 
				referer.toString());
	}
	
	// ----- end of test cases ----- test fixture below ------------------------------------------
	
	private void givenProperReferrerParameters() {
		givenClassName = "de.javagil.columbo.BytecodeInspector"; 
		givenMethodName = "inspect";
		givenMethodDesc = "(Lde/javagil/columbo/ReferenceVisitor;)Lde/javagil/columbo/BytecodeInspector;";
		givenSource = "BytecodeInspector.java";
		givenLineNo = 123;
	}

	private Referrer whenCreatingReferrer() {
		return  new Referrer(givenClassName, givenMethodName, givenMethodDesc, givenSource, givenLineNo);
	}

	private void thenPropertiesOfReferrerReflectTheConstructorArguments(final Referrer referer) {
		assertEquals(givenClassName, referer.className);
		assertEquals(givenMethodName, referer.methodName);
		assertEquals(givenMethodDesc, referer.methodDesc);
		assertEquals(givenSource, referer.sourceFile);
		assertEquals(givenLineNo, referer.line);
	}
}

// TODO move to test util class
/**
 * Wraps code to expect an AssertionError. Unfortunately such can't be handled using @Rule ExpectedException.
 * 
 * @author michael.hoennig@javagil.de
 */
abstract class AssertionContext {

	abstract void when() throws Exception;

	public void thenExpectAssertionError(final String expectedMessage) {
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


