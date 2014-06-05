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

import static de.javagil.columbo.Util.withDefault;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.bytecode.Opcode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit test for class MethodVisitor.
 * 
 * @author michael.hoennig@javagil.de
 */
public class MethodVisitorTest {

	private VisitorContext context = new VisitorContext();
	private ReferenceVisitor refVisitor = new MyReferenceVisitor();
	private MethodVisitor methodVisitor = new MethodVisitor(context, refVisitor);

	private Map<Referrer, Class<?>> foundClassReferences =  new HashMap<Referrer, Class<?>>();
	private Map<Referrer, Method> foundMethodReferences =  new HashMap<Referrer, Method>();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

//	{
//		// temporary code to determine method descriptors for use in test cases
//		String methodDesc = org.objectweb.asm.Type.getMethodDescriptor(
//				org.objectweb.asm.Type.getType(Integer.class), 
//				new org.objectweb.asm.Type[]{org.objectweb.asm.Type.getType(String.class)});
//		System.out.println(methodDesc);
//	}
		
	@Before
	public final void init() {
		context.enteringClass("de.javagil.bytecodeinspector.test.SomeMethodVistorTestClassToBeInspected");
		context.enteringSource("SomeMethodVistorTestClassToBeInspected.java");
	}
	
	@Test
	public final void visitMethodInsnProcessingVirtualMethodCall() {
		
		givenWeAreInspectingMethod("someIntegerMethodTakingAString", "(Ljava/lang/String;)Ljava/lang/Integer;");
		
		whenFindingBytecodeForMethodInvokation(Opcode.INVOKEVIRTUAL, 
				"java/lang/String", "toString", "()Ljava/lang/String;");
		
		thenExpectToFound("someIntegerMethodTakingAString", String.class);
	}
	
	@Test
	public final void visitMethodInsnProcessingCallToUnknownMethod() {
		
		givenWeAreInspectingMethod("someIntegerMethodTakingAString", "(Ljava/lang/String;)Ljava/lang/Integer;");
		
		thenExpectException(InspectionException.class, 
				 "no method found for java.lang.Integer#nonExistantMethod()");
		
		whenFindingBytecodeForMethodInvokation(Opcode.INVOKEVIRTUAL, 
				"java/lang/Integer", "nonExistantMethod", "()Ljava/lang/String;");
	}
	
	@Test
	public final void visitMethodInsnProcessingCallToUnknownClass() {
		
		givenWeAreInspectingMethod("someIntegerMethodTakingAString", "(Ljava/lang/String;)Ljava/lang/Integer;");
		
		thenExpectException(InspectionException.class, 
				"could not determine class in type java/lang/NonExistingClass");
		
		whenFindingBytecodeForMethodInvokation(Opcode.INVOKEVIRTUAL, 
				"java/lang/NonExistingClass", "toString", "()Ljava/lang/String;");
	}
	
	@Test
	public final void findMethodTest() throws NoSuchMethodException, SecurityException {
		assertEquals(
				org.junit.Assert.class.getMethod("assertEquals", array(String.class, long.class, long.class)),
				methodVisitor.findMethod(org.junit.Assert.class, "assertEquals", "((Ljava/lang/String;JJ)V))"));
	}
	
	@Test
	public final void arrayOfTest() throws ClassNotFoundException {
		assertEquals("[I", BytecodeUtil.arrayOf(int.class).getName());
		assertEquals("[Ljava.lang.Integer;", BytecodeUtil.arrayOf(Integer.class).getName());
	}
	
	// ----- end of test cases ----- test fixture below ------------------------------------------
	
	private Class<?>[] array(final Class<?>... classes) {
		return classes;
	}

	private void thenExpectException(final Class<InspectionException> expectedExc, final String expectedMessage) {
		expectedException.expect(expectedExc);
		expectedException.expectMessage(expectedMessage);
	}

	private void givenWeAreInspectingMethod(final String methodName, final String methodDesc) {
		context.enteringMethod(methodName, methodDesc);
	}

	private void whenFindingBytecodeForMethodInvokation(final int callOpcode, final String calledClass, 
			final String calledMethodName, final String calledMethodDesc) {
		methodVisitor.visitMethodInsn(callOpcode, calledClass, calledMethodName, calledMethodDesc);
	}

	private void thenExpectToFound(final String referrerMethod, final Class<String> referrencedClass) {
		String foundReferences = "";
		for (Referrer referrer: foundClassReferences.keySet()) {
			Class<?> foundReferencedClass = foundClassReferences.get(referrer);
			if (referrer.methodName.equals(referrerMethod) && referrencedClass == foundReferencedClass) {
				return;
			}
			foundReferences += "\n" + referrer.methodName + " -> " + foundReferencedClass.getCanonicalName(); 
		}
		fail("expected to find method " + referrerMethod + " using " + referrencedClass + ", but not found. Only found:" + 
				withDefault(foundReferences, " nothing"));
	}

	/** Is called for each found reference.
	 */
	class MyReferenceVisitor extends ReferenceVisitorAdapter {

		@Override
		public void onClassReference(final Referrer referrer, final Class<?> referencedClass) {
			foundClassReferences.put(referrer, referencedClass);
		}

		@Override
		public void onMethodReference(final Referrer referrer, final Method referencedMethod) {
			foundMethodReferences.put(referrer, referencedMethod);
		}
	}
}
