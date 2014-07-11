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

package de.javagil.columbo.testutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;

/**
 * Offers assertion for proper class definition (currently only to validate utility classes).
 * 
 * @author michael.hoennig@javagil.de
 */
public final class AssertClass extends GenericAssert<AssertClass, Class<?>> {
	  private AssertClass(final Class<?> actual) {
	    super(AssertClass.class, actual);
	  }
	 
	  /**
	   * starts assertion on actual, best to make a static import on this method
	   * 
	   * @param actual the class we want to make assertions on
	   * @return an asserter instance
	   */
	  public static AssertClass assertThat(final Class<?> actual) {
	    return new AssertClass(actual);
	  }
	 
	  /**
	   * Verifies that the actual Character's name is equal to the given one.
	   * @param name the given name to compare the actual Character's name to.
	   * @return this assertion object.
	   * @throws Exception if this happens, most likely the constructor of the class threw this Exception
	   */
	  public AssertClass isUtilityClass() throws Exception {
	    isNotNull();
	 
	    Assertions.assertThat(Modifier.isFinal(actual.getModifiers())).
	    		overridingErrorMessage(utlityClass() + " must be final").isTrue();

	    final String mustOnlyHaveOnePrivateDefaultConstructor = " must only have a private default constructor";
		Assertions.assertThat(actual.getDeclaredConstructors().length).
	    		overridingErrorMessage(utlityClass() + mustOnlyHaveOnePrivateDefaultConstructor).isEqualTo(1);

	    Constructor<?> declaredConstructor = actual.getDeclaredConstructor();
	    Assertions.assertThat(Modifier.isPrivate(declaredConstructor.getModifiers())).
	    		overridingErrorMessage(utlityClass() + mustOnlyHaveOnePrivateDefaultConstructor).isTrue();

	    for (final Method method : actual.getDeclaredMethods()) {
	    	Assertions.assertThat(Modifier.isStatic(method.getModifiers())).
	    	overridingErrorMessage(utlityClass() + " must only have static methods");
	    }

	    // the constructor shouldn't do anything, for sure not throw an exception
	    // and we are also getting code coverage this way
	    declaredConstructor.setAccessible(true);
	    declaredConstructor.newInstance();

	    // allow chaining of assertions
	    return this;
	  }

	private String utlityClass() {
		return "utlility " + actual;
	}
}
