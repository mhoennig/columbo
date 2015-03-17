package de.javagil.columbo.api;

import static org.junit.Assert.*;

import org.junit.Test;

import de.javagil.columbo.api.JavaElement;
import de.javagil.columbo.testutil.AssertionContext;



public class JavaElementTest {

	private final JavaElement completeTestee = new JavaElement("java/lang/String", "getBytes", "()[B");
	private final JavaElement completeTesteeAgain = new JavaElement("java/lang/String", "getBytes", "()[B");
	private final JavaElement completeTesteeWithDifferentClass = new JavaElement("java/lang/Long", "getBytes", "()[B");
	private final JavaElement completeTesteeWithDifferentMethod = new JavaElement("java/lang/String", "length", "()I");
	private final JavaElement completeTesteeWithDifferentMethodDesc = new JavaElement("java/lang/String", "getBytes", "(I)[B");

	private final JavaElement someClassTestee = new JavaElement("java/lang/String", null, null);
	private final JavaElement someClassTesteeAgain = new JavaElement("java/lang/String", null, null);
	private final JavaElement anotherClassTestee = new JavaElement("java/lang/Integer", null, null);
	
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
		assertEquals("java.lang.String", someClassTestee.className);
		assertNull(someClassTestee.methodName);
		assertNull(someClassTestee.methodDesc);
	}
	
	@Test
	public final void toContentStringTest() {
		assertEquals("java.lang.String#getBytes", completeTestee.toContentString());
		assertEquals("java.lang.String", someClassTestee.toContentString());
	}
	
	@Test
	public final void hashTest() {
		assertEquals(someClassTestee.hashCode(), someClassTesteeAgain.hashCode() );
		assertEquals(completeTestee.hashCode(), completeTesteeAgain.hashCode());
	}
	
	@Test
	public final void equalsTest() {

		// wrong parameter type
		assertFalse(someClassTestee.equals(null));
		assertFalse(someClassTestee.equals("dummy"));
		
		// same instance
		assertTrue(someClassTestee.equals(someClassTestee));
		assertTrue(completeTestee.equals(completeTestee));

		// same values
		assertTrue(someClassTestee.equals(someClassTesteeAgain));
		assertTrue(completeTestee.equals(completeTesteeAgain));

		// completely different
		assertFalse(someClassTestee.equals(completeTestee));
		assertFalse(completeTestee.equals(someClassTestee));

		// subtle differences
		assertFalse(someClassTestee.equals(anotherClassTestee));
		assertFalse(completeTestee.equals(completeTesteeWithDifferentClass));
		assertFalse(completeTestee.equals(completeTesteeWithDifferentMethod));	
		assertFalse(completeTestee.equals(completeTesteeWithDifferentMethodDesc));
	}

}
