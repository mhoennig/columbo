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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 *  Callback interface which is called when a reference to a Java class or method was found in the inspected code. 
 */
public interface ReferenceVisitor {

	/** Is called if a class usage was found (new, instanceof, as parameter, ... anything).
	 * 
	 * @param referrer specifies where the reference class is used
	 * @param referencedClass the class which was used in the given package
	 */
	void onClassReference(final Referrer referrer, final Class<?> referencedClass);
	
	/** Is called if a method call was found.
	 * 
	 * @param referrer specifies where the reference class is used
	 * @param referencedMethod the method which was used in the given package
	 */
	void onMethodCall(final Referrer referrer, final Method referencedMethod);

	/**
	 * Is called when a method could not be found.  
	 * This is usually the case when the class which should contain the method,
	 * is not in the CLASSPATH in multiple incompatible version.
	 * <p>This usually indicates a CLASSPATH inconsistency.</p> 
	 * 
	 * @param clazz the class of the instance of which a method was called
	 * @param name name of the called method
	 * @param paramTypes parameter types of the called method
	 */
	void onMethodNotFound(final Class<?> clazz, String name, final Class<?>[] paramTypes);

	/**
	 * Is called when a constructor invocation was determined.
	 * 
	 * @param referrer specifies where the reference class is used
	 * @param constructor the constructor which is called
	 */
	void onConstructorCall(final Referrer referrer, final Constructor<?> constructor);

	/**
	 * Is called when a constructor could not be found.  
	 * This is usually the case when the class which should contain the method,
	 * is not in the CLASSPATH in multiple incompatible version.
	 * <p>This usually indicates a CLASSPATH inconsistency.</p> 
	 * 
	 * @param clazz the class for which an initialization call was found
	 * @param paramTypes parameter types of the called constructor
	 */
	void onConstructorNotFound(final Class<?> clazz, final Class<?>[] paramTypes);

	/** Is called if a field access was found.
	 * 
	 * @param referrer specifies where the reference class is used
	 * @param referencedField the method which was used in the given package
	 */
	void onFieldAccess(Referrer referrer, Field referencedField);

	
	/** Is called if a field which is accessed was not found.
	 *  <p>This usually indicates a CLASSPATH inconsistency.</p> 
	 * 
	 * @param referrer specifies where the reference class is used
	 * @param referencedField the name of the field which was used in the given package
	 */
	void onFieldNotFound(Class<?> referrer, String referencedField);
}