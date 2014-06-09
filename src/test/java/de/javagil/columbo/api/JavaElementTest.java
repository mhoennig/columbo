package de.javagil.columbo.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.javagil.columbo.api.JavaElement;
import de.javagil.columbo.testutil.AssertionContext;



public class JavaElementTest {

	private final JavaElement testee = new JavaElement("java/lang/String", "getBytes", "()[B");
	
	@Test
	public final void constructorTest() {
		assertEquals("java.lang.String", testee.className);
		assertEquals("getBytes", testee.methodName);
		assertEquals("()[B", testee.methodDesc);
	}
	
	@Test
	public final void constructorWithInvalidClassNameTest() {
		new AssertionContext() {
			
			@Override
			protected void when() throws Exception {
				new JavaElement("java.lang.String", "getBytes", "()[B");
			}
		}.thenExpectAssertionError("not a proper internal Java class name ('/' as separator, not '.')");
	}
	
	@Test
	public final void toContentStringTest() {
		assertEquals("java.lang.String#getBytes", testee.toContentString());		
	}

}
