package de.javagil.columbo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.javagil.columbo.api.JavaElement;
import de.javagil.columbo.testutil.AssertionContext;



public class JavaElementTest {

	private final JavaElement completeTestee = new JavaElement("java/lang/String", "getBytes", "()[B");
	private final JavaElement justClassTestee = new JavaElement("java/lang/Integer", null, null);
	
	@Test
	public final void constructorTest() {
		assertEquals("java.lang.String", completeTestee.className);
		assertEquals("getBytes", completeTestee.methodName);
		assertEquals("()[B", completeTestee.methodDesc);
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
	public final void constructorWithoutMethodTest() {
		assertEquals("java.lang.Integer", justClassTestee.className);
		assertNull(justClassTestee.methodName);
		assertNull(justClassTestee.methodDesc);
	}
	
	@Test
	public final void toContentStringTest() {
		assertEquals("java.lang.String#getBytes", completeTestee.toContentString());
		assertEquals("java.lang.Integer", justClassTestee.toContentString());
	}

}
