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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.javagil.columbo.api.JavaElement;
import de.javagil.columbo.api.ReferenceVisitor;
import de.javagil.columbo.api.ReferenceVisitorAdapter;
import de.javagil.columbo.api.Referrer;
import de.javagil.columbo.api.VisitorContext;
import de.javagil.columbo.internal.BufferedClassInputStreamTest.SomeInnerClass;


/**
 * Unit test for class {@link ClassVisotor}.
 * 
 * @author michael.hoennig@javagil.de
 *
 */
public class ClassVisitorTest {
	
	private static final int MIN_EXPECTED_NUMBER_OF_METHOD_OVERRIDES = 10;

	private VisitorContext ctx = new VisitorContext();
	
	@Mock
	private ReferenceVisitor refVisitorMock = new ReferenceVisitorAdapter();
	
	private ClassVisitor testee;
	
	@Before
	public final void init() throws MalformedURLException {
		MockitoAnnotations.initMocks(this);
		ctx.enteringResource(new URL("file:///dummy.java"));
		testee = new ClassVisitor(ctx, refVisitorMock);
	}

	@Test
	public final void visitTest() {
		testee.visit(1, 2, "java/lang/String", null, null, null);
		assertEquals("java/lang/String", ctx.getCurrentClassName());
		
		testee.visitSource("String.java", null);
		assertEquals("java.lang.String", ctx.toReferrer().toContentString());
		
		testee.visitMethod(1, "length", "()I", "signature?", null);
		assertEquals("java.lang.String#length", ctx.toReferrer().toContentString());
		
		testee.visitEnd();
		assertNull(ctx.getCurrentClassName());
	}
	
	/**
	 * Tests whether {@link ClassVisitor#inspect} issues callbacks at all.
	 */
	@Test
	public final void inspectTest() throws MalformedURLException, SecurityException, NoSuchMethodException {
		ctx.leavingResource(); // for this test we need a clean environment
		Set<String> classNamesToInspect = new HashSet<String>();
		classNamesToInspect.add(String.class.getName());
		classNamesToInspect.add(Long.class.getName());
		
		testee.inspect(classNamesToInspect);

		// @formatter:off
		ArgumentCaptor<Method> methodCaptor = ArgumentCaptor.forClass(Method.class);
		Mockito.verify(refVisitorMock, Mockito.atLeast(MIN_EXPECTED_NUMBER_OF_METHOD_OVERRIDES)).
			onMethodOverride(Mockito.any(Referrer.class), methodCaptor.capture());
		Assertions.assertThat(methodCaptor.getAllValues()).contains( 
				java.lang.Number.class.getMethod("byteValue"),
				java.lang.CharSequence.class.getMethod("length") );
		// @formatter:on
	}
	
	/** Tests whether ClassVisitor.onMethodOverride(...) is called 
	 *  if a method is overridden which has default access in the super class.
	 */
	@Test
	public final void visitMethodCallsOnMethodOverrideIfDefaultInSuper() throws Exception {
		whenVisiting(SomeClientClass.class, "someMethodWhichIsDefaultInSuper", "(I,Ljava.lang.String;)I");
		onMethodOverrideCalled(SomeFrameworkClass.class, "someMethodWhichIsDefaultInSuper", int.class, String.class);
		onMethodOverrideNotCalledForAnyOtherMethod();
	}
	
	@Test
	public final void visitMethodDoesNotCallOnMethodOverrideIfPrivateInSuper() throws Exception {
		whenVisiting(SomeClientClass.class, "someMethodWhichIsPrivateInSuper", "(I,Ljava.lang.String;)I");
		onMethodOverrideNotCalled(SomeFrameworkClass.class, "someMethodWhichIsPrivateInSuper", int.class, String.class);
		onMethodOverrideNotCalledForAnyOtherMethod();
	}
	
	@Test
	public final void visitMethodCallsOnMethodOverrideIfProtectedInSuper() throws Exception {
		whenVisiting(SomeClientClass.class, "someMethodWhichIsProtectedInSuper", "(I,Ljava.lang.String;)I");
		onMethodOverrideCalled(SomeFrameworkClass.class, "someMethodWhichIsProtectedInSuper", int.class, String.class);
		onMethodOverrideNotCalledForAnyOtherMethod();
	}

	@Test
	public final void visitMethodCallsOnMethodOverrideIfPublicInSuper() throws Exception {
		whenVisiting(SomeClientClass.class, "someMethodWhichIsPublicInSuper", "(I,Ljava.lang.String;)I");
		onMethodOverrideCalled(SomeFrameworkClass.class, "someMethodWhichIsPublicInSuper", int.class, String.class);
		onMethodOverrideNotCalledForAnyOtherMethod();
	}

	@Test
	public final void visitMethodCallsOnMethodOverrideIfDeclaredInIndirectInterface() throws Exception {
		whenVisiting(SomeClientClass.class, "someMethodFromIndirectInterface", "(I,Ljava.lang.String;)I");
		onMethodOverrideCalled(SomeFrameworkInterface.class, "someMethodFromIndirectInterface", int.class, String.class);
		onMethodOverrideNotCalledForAnyOtherMethod();
	}
	
	@Test
	public final void visitMethodCallsOnMethodOverrideIfDeclaredInDirectInterfaceAndSuper() throws Exception {
		whenVisiting(SomeClientClass.class, "someMethodFromDirectInterface", "(I,Ljava.lang.String;)I");
		onMethodOverrideCalled(SomeClientSuperclass.class, "someMethodFromDirectInterface", int.class, String.class);
		onMethodOverrideCalled(SomeClientInterface.class, "someMethodFromDirectInterface", int.class, String.class);
		onMethodOverrideNotCalledForAnyOtherMethod();
	}
	
	// --- end of test cases, just fixture code below ---

	private void whenVisiting(Class<?> clazz, String methodName, String methodDesc) {
		String resourceClassName = BytecodeUtil.getResourceClassName(clazz).substring(1).replaceFirst("\\.class$", "");
		ctx.enteringClass(resourceClassName);
		ctx.enteringSource(resourceClassName + ".java");
		testee.visitMethod(1, methodName, methodDesc, null, null);
	}

	private void onMethodOverrideCalled(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException, NoSuchMethodException {
		Referrer expectedReferrer = ctx.toReferrer();
		Method expectedMethod = clazz.getDeclaredMethod(methodName, paramTypes);
		Mockito.verify(refVisitorMock).onMethodOverride(expectedReferrer, expectedMethod);
	}

	private void onMethodOverrideNotCalled(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException, NoSuchMethodException {
		Referrer expectedReferrer = ctx.toReferrer();
		Method expectedMethod = clazz.getDeclaredMethod(methodName, paramTypes);
		Mockito.verify(refVisitorMock, Mockito.never()).onMethodOverride(expectedReferrer, expectedMethod);
	}

	private void onMethodOverrideNotCalledForAnyOtherMethod() throws SecurityException, NoSuchMethodException {
		Mockito.verify(refVisitorMock, Mockito.never()).onMethodCall(Mockito.any(Referrer.class), Mockito.any(Method.class));
	}
}

class SomeClientClass extends SomeClientSuperclass implements SomeClientInterface {

	int someMethodWhichIsDefaultInSuper(final int count, String name) {
		return 0;
	}

	int someMethodWhichIsPrivateInSuper(final int count, String name) {
		return 0;
	}

	protected int someMethodWhichIsProtectedInSuper(final int count, String name) {
		return 0;
	}

	public int someMethodWhichIsPublicInSuper(final int count, String name) {
		return 0;
	}
	
	public int someMethodFromIndirectInterface(final int count, String name) {
		return 0;
	}
	
	public int someMethodFromDirectInterface(final int count, String name) {
		return 0;
	}

}

abstract class SomeClientSuperclass extends SomeFrameworkClass {
	public int someMethodFromDirectInterface(final int count, String name) {
		return 0;
	}
}

abstract class SomeFrameworkClass implements SomeFrameworkInterface {

	int someMethodWhichIsDefaultInSuper(final int count, String name) {
		return 0;
	}

	private int someMethodWhichIsPrivateInSuper(final int count, String name) {
		return 0;
	}

	protected int someMethodWhichIsProtectedInSuper(final int count, String name) {
		return 0;
	}

	public int someMethodWhichIsPublicInSuper(final int count, String name) {
		return 0;
	}
}

interface SomeClientInterface {
	int someMethodFromDirectInterface(final int count, String name);
}

interface SomeFrameworkInterface {
	int someMethodFromIndirectInterface(final int count, String name);
}


