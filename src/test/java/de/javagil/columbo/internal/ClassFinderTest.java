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

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.commons.EmptyVisitor;

import de.javagil.columbo.internal.ClassFinder;
import de.javagil.columbo.testbed.general.SomeMethodVistorTestClassToBeCalled;
import de.javagil.columbo.testbed.general.SomeMethodVistorTestClassToBeInspected;
import de.javagil.columbo.testbed.general.SomeTestClass;
import de.javagil.columbo.testbed.general.SomeTestInterface;
import de.javagil.columbo.testbed.general.good.SomeCleanClass;

/**
 * JUnit test for ClassFinderTest.
 * 
 * @author michael.hoennig@javagil.de
 */
public class ClassFinderTest {

	 @Test
	 public final void findAllClassNames() {
		 ClassFinder cf = new ClassFinder(SomeTestClass.class.getPackage().getName());
		 Set<String> allClassNamesInPackage = cf.findAllClassNames();
		 assertEquals(5, allClassNamesInPackage.size());
		 
		 assertThat(allClassNamesInPackage).containsOnly(
 				SomeTestClass.class.getCanonicalName(), 
 				SomeTestInterface.class.getCanonicalName(),
 				SomeMethodVistorTestClassToBeCalled.class.getCanonicalName(),
 				SomeMethodVistorTestClassToBeInspected.class.getCanonicalName(),
	 			SomeCleanClass.class.getCanonicalName());
	 }
	 
	 @Test
	 @Ignore("FIXME works fine in Eclipse but not with Maven")
	 public final void findAllClassNameFromAPackageInAJarFile() {
		 // TODO create a jar with fixed content just for test purposes
		 ClassFinder cf = new ClassFinder("org.objectweb.asm.commons");
		 Set<String> allClassNamesInPackage = cf.findAllClassNames();
		 assertEquals(11, allClassNamesInPackage.size()); // might change with version
		 
		 assertThat(allClassNamesInPackage).contains(
 				EmptyVisitor.class.getCanonicalName());
	 }
	 
	  
}
