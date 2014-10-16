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

import java.lang.reflect.Method;
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
				int.class, 
				java.lang.Object.class, // for the superclass constructor call
				java.lang.Integer.class,
				java.lang.String.class,
				java.lang.annotation.Retention.class, // annotation of @SomeTestAnnotation
				java.lang.annotation.Annotation.class, // super of @SomeTestAnnotation
				SomeAnnotation.class, // via @SomeTestClass
				SomeCleanClass.class, // self reference from it's own init-code
				SomeTestClass.class); // self reference from it's own init-code  

	}
}

/** 
 * Collects bytecode references. 
 */
class MyReferenceVisitor extends ReferenceVisitorAdapter {

	final Set<String> refererClasses = new HashSet<String>();
	final Set<Class<?>> referencedClasses = new HashSet<Class<?>>();

	@Override
	public void onClassReference(final Referrer referrer, final Class<?> referencedClass) {
		refererClasses.add(referrer.getJavaElement().className);
		referencedClasses.add(referencedClass);
	}

	@Override
	public void onMethodCall(final Referrer referrer, final Method referencedMethod) {
		refererClasses.add(referrer.getJavaElement().className);
		referencedClasses.add(referencedMethod.getDeclaringClass());
	}
}

