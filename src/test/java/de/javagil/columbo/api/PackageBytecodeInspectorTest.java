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

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.javagil.columbo.testbed.general.SomeAnnotation;
import de.javagil.columbo.testbed.general.SomeTestClass;
import de.javagil.columbo.testbed.general.good.SomeCleanClass;

/**
 * Unit test for class {@link PackageBytecodeInspector}.
 * 
 * @author michael.hoennig@javagil.de
 */
public class PackageBytecodeInspectorTest {

	@Test
	public final void ctorPackageBytecodeInspector() {
		final MyReferenceVisitor referenceVisitor = new MyReferenceVisitor();

		PackageBytecodeInspector inspector = new PackageBytecodeInspector("de.javagil.columbo.testbed.general");
		inspector.inspect(referenceVisitor);
		
		assertThat(referenceVisitor.refererClasses).containsOnly(
				de.javagil.columbo.testbed.general.SomeTestClass.class.getCanonicalName(),
				de.javagil.columbo.testbed.general.SomeMethodVistorTestClassToBeInspected.class.getCanonicalName(),
				de.javagil.columbo.testbed.general.SomeMethodVistorTestClassToBeCalled.class.getCanonicalName(),
				de.javagil.columbo.testbed.general.good.SomeCleanClass.class.getCanonicalName(),
				de.javagil.columbo.testbed.general.SomeAnnotation.class.getCanonicalName());
		assertThat(referenceVisitor.referencedClasses).containsOnly(
				int.class, int[].class, 
				byte[].class,
				java.lang.Object.class, // for the superclass constructor call
				java.lang.Integer.class, java.lang.Integer[].class,
				java.lang.String.class,
				java.lang.annotation.Retention.class, // annotation of @SomeTestAnnotation
				java.lang.annotation.Annotation.class, // super of @SomeTestAnnotation
				SomeAnnotation.class, // via @SomeTestClass
				SomeCleanClass.class, // self reference from it's own init-code
				SomeTestClass.class); // self reference from it's own init-code  

	}
	
	@Test
	public void byteTest() {
		assertNotSame(byte[].class, byte.class);
		assertNotEquals(byte[].class.toString(), byte.class.toString());
	}
	
	@Test
	public void anonymousClassTest() throws ClassNotFoundException {
		final MyReferenceVisitor referenceVisitor = new MyReferenceVisitor();
		PackageBytecodeInspector inspector = new PackageBytecodeInspector("de.javagil.columbo.testbed.anonymous");
		inspector.inspect(referenceVisitor);

		assertThat(referenceVisitor.refererClasses).containsOnly(
				de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.class.getName(),

				// refers java.lang.Object as superclass, as such it appears here:
				de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.ClassToSubclass.class.getName(),
				
				// does not refer anything by by itself, therefore does not appear here
				// de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.InterfaceToSubclass.class.getName(),
				
				anon(de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.class, 1).getName(),
				anon(de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.class, 2).getName());
		assertThat(referenceVisitor.referencedClasses).containsOnly(
				de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.class,
				anon(de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.class, 1),
				anon(de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.class, 2),
				de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.ClassToSubclass.class,
				de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.InterfaceToSubclass.class,
				Color.class, Object.class, void.class);
		assertThat(referenceVisitor.referencedFields).containsOnly(
				"anonExtendingAClass", "anonImplementingAnInterface", "this$0", "RED", "GREEN");
		
		// FIXME: overrides in anonymous classes are not yet recognized
		assertThat(referenceVisitor.overriddenMethodClasses).containsOnly(
				de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.ClassToSubclass.class,
				de.javagil.columbo.testbed.anonymous.TestClassWithAnonymousInnerClasses.InterfaceToSubclass.class);
	}

	private Class<?> anon(Class<?> outerClass, int no) throws ClassNotFoundException {
		return Class.forName(outerClass.getCanonicalName()+"$"+no);
	}
}

/** 
 * Collects bytecode references. 
 */
class MyReferenceVisitor extends ReferenceVisitorAdapter {

	final Set<String> referencedFields = new HashSet<String>();
	final Set<String> refererClasses = new HashSet<String>();
	final Set<Class<?>> referencedClasses = new HashSet<Class<?>>();
	final Set<Class<?>> overriddenMethodClasses = new HashSet<Class<?>>();

	@Override
	public void onClassReference(final Referrer referrer, final Class<?> referencedClass) {
		refererClasses.add(referrer.getJavaElement().className);
		referencedClasses.add(referencedClass);
	}
	
	@Override
	public void onMethodOverride(Referrer referrer, Method referencedMethod) {
		refererClasses.add(referrer.getJavaElement().className);
		overriddenMethodClasses.add(referencedMethod.getDeclaringClass());
	}

	@Override
	public void onMethodCall(final Referrer referrer, final Method referencedMethod) {
		refererClasses.add(referrer.getJavaElement().className);
		referencedClasses.add(referencedMethod.getDeclaringClass());
	}
	
	@Override
	public void onFieldAccess(Referrer referrer, Field referencedField) {
		referencedFields.add(referencedField.getName());
	}
}

