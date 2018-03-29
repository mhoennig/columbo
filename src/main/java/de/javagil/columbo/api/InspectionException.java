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

import de.javagil.columbo.internal.BytecodeUtil;

/**
 * Thrown on unexpected exceptions during class loading or bytecode reading.
 * 
 * @author michael.hoennig@javagilde 
 */
public class InspectionException extends RuntimeException {

	private static final long serialVersionUID = 7452661942714571624L;
	
	private static final String PROBABLY_INCONSISTENT_CLASSPATH = " - most likely inconsistent CLASSPATH (some class compiled against a class incompatible to what's now in CLASSPATH)";

	/**
	 * Creates an instance based on an original (mostly non Runtime-) exception
	 * 
	 * @param exc the original exception
	 */
	public InspectionException(final Exception exc) {
		super(exc);
	}

	/**
	 * Creates an instance based on a message.
	 * 
	 * @param message describes the exception
	 */
	public InspectionException(final String message) {
		super(message);
	}

	/**
	 * Creates an instance based on a message and an original exception.
	 * 
	 * @param message describes the exception
	 * @param exc the original exception
	 */
	public InspectionException(final String message, final Throwable exc) {
		super(message, exc);
	}

	/**
	 * Wraps checked exceptions into a BytecodeInspectionException.
	 * 
	 * @param exc original exception
	 * @return the original exception if it is a {@link java.util.RuntimeException}, 
	 * 			a new BytecodeInspectionException if the original exception is a checked exception
	 */
	static RuntimeException asRuntimeException(final Exception exc) {
		if (exc instanceof RuntimeException) {
			return (RuntimeException) exc;
		} else {
			return new InspectionException(exc);
		}
	}


	/**
	 * Creates an {@link InspectionException} for a class which was not found.
	 * In this case the version of the target class in the CLASSPATH is most likely
	 * incompatible to the version from compile time. 
	 * 
	 * @param referrer client code which refers to the element not found 
	 * @param cause NoClassDefFoundError or ClassNotFoundException
	 * @return an {@link InspectionException}
	 */
	public static InspectionException createClassNotFoundException(final Referrer referrer, final Throwable cause) {
		assert cause != null : "cuase must not be null";
		
		return new InspectionException(referrer.toContentString() + " uses non existing class: " + cause.getMessage() + PROBABLY_INCONSISTENT_CLASSPATH);
	}

	/**
	 * Creates an {@link InspectionException} for a method which was not found.
	 * In this case the version of the target class in the CLASSPATH is most likely
	 * incompatible to the version from compile time. 
	 *
	 * @param referrer client code which refers to the element not found 
	 * @param clazz the class on which a method was called
	 * @param name the name of the method
	 * @param paramTypes the parameter types
	 * @return an {@link InspectionException}
	 */
	public static InspectionException createMethodNotFoundException(
				Referrer referrer, final Class<?> clazz, final String name, final Class<?>[] paramTypes) {
		assert clazz != null : "class must not be null";
		assert name != null : "name must not be null";
		
		return new InspectionException(referrer.toContentString() + " uses non existing method: " + clazz.getName() + "#" + name + 
					asString(paramTypes) + PROBABLY_INCONSISTENT_CLASSPATH);
	}

	/**
	 * Creates an {@link InspectionException} for a constructor which was not found.
	 * In this case the version of the target class in the CLASSPATH is most likely
	 * incompatible to the version from compile time. 
	 * 
	 * @param referrer client code which refers to the element not found 
	 * @param clazz the class on which a constructor was invoked
	 * @param paramTypes the parameter types
	 * @return an {@link InspectionException}
	 */
	public static InspectionException createConstructorNotFoundException(Referrer referrer, final Class<?> clazz, final Class<?>[] paramTypes) {
		assert clazz != null : "class must not be null";
		
		return new InspectionException(referrer.toContentString() + " uses non existing constructor: " + clazz.getName() +
					asString(paramTypes) + PROBABLY_INCONSISTENT_CLASSPATH);	
	}

	/**
	 * Creates an {@link InspectionException} for a field which was not found.
	 * In this case the version of the target class in the CLASSPATH is most likely
	 * incompatible to the version from compile time. 
	 * 
	 * @param referrer client code which refers to the element not found 
	 * @param clazz the class on which a method was called
	 * @param name the name of the method
	 * @return an {@link InspectionException}
	 */
	public static InspectionException createFieldNotFoundException(Referrer referrer, Class<?> clazz, String name) {
		assert clazz != null : "class must not be null";
		assert name != null : "name must not be null";
		
		return new InspectionException(referrer.toContentString() + " uses non existing field: " + clazz.getName() + "#" + name + PROBABLY_INCONSISTENT_CLASSPATH);
	}
	
	private static String asString(final Class<?>[] paramTypes) {
		if (paramTypes == null || paramTypes.length == 0) {
			return "()";
		}
		
		String asString = "(";
		for (Class<?> clazz: paramTypes) {
			asString += BytecodeUtil.getJavaClassName(clazz) + ", ";
		}
		return asString.substring(0, asString.length() - 2) + ")";
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other != null && other.getClass() == getClass() ) {
			return toString().equals(other.toString());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
