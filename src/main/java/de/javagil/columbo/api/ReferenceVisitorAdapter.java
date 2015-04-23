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
 * Empty implementation for interface {@link ReferenceVisitor}.
 * By extending this, you only need to implement the methods you really need. 
 * 
 * @author michael.hoennig@javagil.de
 */
public class ReferenceVisitorAdapter implements ReferenceVisitor {

	@Override
	public void onClassReference(final Referrer referrer, final Class<?> referencedClass) {
	}

	@Override
	public void onClassNotFound(final Referrer referrer, final Throwable cause) {
		throw new InspectionException("most likely inconsistent CLASSPATH", cause);
	}

	@Override
	public void onMethodOverride(final Referrer referrer, final Method referencedMethod) {
	}

	@Override
	public void onMethodCall(final Referrer referrer, final Method referencedMethod) {
	}

	// CHECKSTYLE:OFF DesignForExtension the exception is not needed when method is overridden
	@Override
	public void onMethodNotFound(final Class<?> clazz, final String name, final Class<?>[] paramTypes) {
		throw InspectionException.createMethodNotFoundException(clazz, name, paramTypes);
	}
	// CHECKSTYLE:ON

	@Override
	public void onConstructorCall(final Referrer referrer, final Constructor<?> constructor) {
	}

	// CHECKSTYLE:OFF DesignForExtension the exception is not needed when method is overridden
	@Override
	public void onConstructorNotFound(final Class<?> clazz, final Class<?>[] paramTypes) {
		throw InspectionException.createConstructorNotFoundException(clazz, paramTypes);
	}
	// CHECKSTYLE:ON

	@Override
	public void onFieldAccess(Referrer referrer, Field referencedField) {
	}

	// CHECKSTYLE:OFF DesignForExtension the exception is not needed when method is overridden
	@Override
	public void onFieldNotFound(final Class<?> clazz, final String name) {
		throw InspectionException.createFieldNotFoundException(clazz, name);
	}
	// CHECKSTYLE:ON
}
