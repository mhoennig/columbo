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
package de.javagil.columbo.internal;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import de.javagil.columbo.api.JavaElement;
import de.javagil.columbo.api.Referrer;
import de.javagil.columbo.internal.ReferrerImpl;
import de.javagil.columbo.testutil.AssertionContext;


/**
 * JUnit test for Referrer.
 * 
 * @author michael.hoennig@javagil.de
 */
public class ReferrerImplTest {

	private String givenInternalClassName; 
	private String givenClassName; 
	private String givenMethodName;
	private String givenMethodDesc;
	private URL givenResourceURL;
	private String givenSource;
	private Integer givenLineNo;
	private URL someResource;
	private URL anotherResource;

	@Test
	public final void constructRefererWithValidArguments() throws MalformedURLException {
		givenProperReferrerParametersWithLineNo(123);
		ReferrerImpl referer = whenCreatingReferrer();
		thenPropertiesOfReferrerReflectTheConstructorArguments(referer);
	}

	@Test
	public final void constructReferrerWithInvalidClassName() {
		final String aClassNameWithDots = "de.javagil.columbo.BytecodeInspector";
		
		new AssertionContext() {
			@Override
			public void when() {
				new ReferrerImpl(new JavaElement(aClassNameWithDots, "test", null), null, null, null);	
			}

		} .thenExpectAssertionError("not a proper internal Java class name ('/' as separator, not '.')");
	}
	
	@Test
	public final void toStringTest() throws MalformedURLException {
		givenProperReferrerParametersWithLineNo(123);
		ReferrerImpl referer = whenCreatingReferrer();
		assertEquals(
				"de.javagil.columbo.internal.ReferrerImpl[de.javagil.columbo.api.BytecodeInspector#inspect:123]", 
				referer.toString());
	}
	
	@Test
	public final void toContentStringTest() throws MalformedURLException {
		givenProperReferrerParametersWithLineNo(123);
		ReferrerImpl referer = whenCreatingReferrer();
		assertEquals(
				"de.javagil.columbo.api.BytecodeInspector#inspect:123", 
				referer.toContentString());
	}
	
	@Test
	public final void toContentStringWithoutLineNoTest() throws MalformedURLException {
		givenProperReferrerParametersWithoutLineNo();
		ReferrerImpl referer = whenCreatingReferrer();
		assertEquals(
				"de.javagil.columbo.api.BytecodeInspector#inspect", 
				referer.toContentString());
	}
	
	@Test
	public final void hashTest() {
		JavaElement someJavaElement = new JavaElement("test/Dummy", "testMethodA", "()V");
		
		// equal but not same instances with all parameters in either
		assertSameHash( new ReferrerImpl(someJavaElement, someResource, "Foo.java", 42),
						new ReferrerImpl(someJavaElement, someResource, "Foo.java", 42) );
		
		// variations of Java element parameter
		assertSameHash( new ReferrerImpl(someJavaElement, null, null, null),
						new ReferrerImpl(someJavaElement, null, null, null) );

		// variations of resource parameter
		assertSameHash( new ReferrerImpl(someJavaElement, someResource, null, null),
						new ReferrerImpl(someJavaElement, someResource, null, null) );

		// variations of source parameter
		assertSameHash( new ReferrerImpl(someJavaElement, someResource, "Foo.java", null),
					new ReferrerImpl(someJavaElement, someResource, "Foo.java", null) );

		// variations of line parameter
		assertSameHash( new ReferrerImpl(someJavaElement, null, null, 42),
						new ReferrerImpl(someJavaElement, null, null, 42) );
	}
	
	@Test
	public final void equalsTest() {
		JavaElement someJavaElement = new JavaElement("test/Dummy", "testMethodA", "()V");
		JavaElement anotherJavaElement = new JavaElement("test/Dummy", "testMethodB", "()V");
		
		// same instance
		ReferrerImpl someReferrer = new ReferrerImpl(someJavaElement, null, null, null);
		assertTrue(someReferrer.equals(someReferrer));
		
		// equal but not same instances with all parameters in either
		assertTrue( new ReferrerImpl(someJavaElement, someResource, "Foo.java", 42).equals(
					new ReferrerImpl(someJavaElement, someResource, "Foo.java", 42) ) );
		
		// wrong type
		assertFalse(new ReferrerImpl(someJavaElement, null, null, null).equals(null));
		assertFalse(new ReferrerImpl(someJavaElement, null, null, null).equals(""));
		
		// variations of Java element parameter
		assertTrue( new ReferrerImpl(someJavaElement, null, null, null).equals(
					new ReferrerImpl(someJavaElement, null, null, null) ) );
		assertFalse( new ReferrerImpl(someJavaElement, null, null, null).equals(
					 new ReferrerImpl(anotherJavaElement, null, null, null) ) );

		// variations of resource parameter
		assertTrue( new ReferrerImpl(someJavaElement, someResource, null, null).equals(
					new ReferrerImpl(someJavaElement, someResource, null, null) ) );
		assertFalse( new ReferrerImpl(someJavaElement, someResource, null, null).equals(
					 new ReferrerImpl(someJavaElement, null, null, null) ) );
		assertFalse( new ReferrerImpl(someJavaElement, someResource, null, null).equals(
					 new ReferrerImpl(someJavaElement, anotherResource, null, null) ) );
		assertFalse( new ReferrerImpl(someJavaElement, null, null, null).equals(
					 new ReferrerImpl(someJavaElement, someResource, null, null) ) );

		// variations of source parameter
		assertTrue( new ReferrerImpl(someJavaElement, someResource, "Foo.java", null).equals(
					new ReferrerImpl(someJavaElement, someResource, "Foo.java", null) ) );
		assertFalse( new ReferrerImpl(someJavaElement, someResource, "Foo.java", null).equals(
					 new ReferrerImpl(someJavaElement, someResource, null, null) ) );
		assertFalse( new ReferrerImpl(someJavaElement, someResource, "Foo.java", null).equals(
					 new ReferrerImpl(someJavaElement, someResource, "Bar.java", null) ) );
		assertFalse( new ReferrerImpl(someJavaElement, someResource, null, null).equals(
					 new ReferrerImpl(someJavaElement, someResource, "Foo.java", null) ) );

		// variations of line parameter
		assertTrue( new ReferrerImpl(someJavaElement, null, null, 42).equals(
					new ReferrerImpl(someJavaElement, null, null, 42) ) );
		assertFalse( new ReferrerImpl(someJavaElement, null, null, 42).equals(
					 new ReferrerImpl(someJavaElement, null, null, null) ) );
		assertFalse( new ReferrerImpl(someJavaElement, null, null, 42).equals(
					 new ReferrerImpl(someJavaElement, null, null, 99) ) );
		assertFalse( new ReferrerImpl(someJavaElement, null, null, null).equals(
					 new ReferrerImpl(someJavaElement, null, null, 42) ) );
	}
	
	// ----- end of test cases ----- test fixture below ------------------------------------------

	private void givenProperReferrerParametersWithLineNo(int lineNo) throws MalformedURLException {
		givenProperReferrerParameters(lineNo);
	}
	
	private void givenProperReferrerParametersWithoutLineNo() throws MalformedURLException {
		givenProperReferrerParameters(null);
	}
		
	private void givenProperReferrerParameters(Integer lineNo) throws MalformedURLException {
		givenInternalClassName = "de/javagil/columbo/api/BytecodeInspector"; 
		givenClassName = "de.javagil.columbo.api.BytecodeInspector"; 
		givenMethodName = "inspect";
		givenMethodDesc = "(Lde/javagil/columbo/api/ReferenceVisitor;)Lde/javagil/columbo/api/BytecodeInspector;";
		givenResourceURL = new URL("file://some/path/somejar.jar");
		givenSource = "BytecodeInspector.java";
		givenLineNo = lineNo;
	}

	private ReferrerImpl whenCreatingReferrer() {
		return new ReferrerImpl(
						new JavaElement(givenInternalClassName, givenMethodName, givenMethodDesc), 
								givenResourceURL, givenSource, givenLineNo);
	}

	private void thenPropertiesOfReferrerReflectTheConstructorArguments(final Referrer referer) {
		assertEquals(givenClassName, referer.getJavaElement().className);
		assertEquals(givenMethodName, referer.getJavaElement().methodName);
		assertEquals(givenMethodDesc, referer.getJavaElement().methodDesc);
		assertEquals(givenResourceURL, referer.getResourceURL());
		assertEquals(givenSource, referer.getSourceFile());
		assertEquals(givenLineNo, referer.getLineNo());
	}
	
	private void assertSameHash(ReferrerImpl referrerImpl1, ReferrerImpl referrerImpl2) {
		assertEquals(referrerImpl1.hashCode(), referrerImpl2.hashCode());
	}

	@Before
	public void init() throws MalformedURLException {
		someResource = new URL("file:////dummy/some.jar");
		anotherResource = new URL("file:////dummy/another.jar");
	}
}


