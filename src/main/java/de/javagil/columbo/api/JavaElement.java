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

import java.lang.reflect.Method;

import javax.annotation.Generated;

import de.javagil.columbo.internal.BytecodeUtil;

/**
 * Describes the java element (class initializer, instance initializer, method) 
 * which refers to some found usage.
 * 
 * @author michael.hoennig@javagil.de
 */
public class JavaElement {
	
	public final String className;
    public final String methodName;
    public final String methodDesc;

	public JavaElement(final String internalClassName, final String methodName, final String methodDesc) {
		assert internalClassName.indexOf('.') == -1 : "not a proper internal Java class name ('/' as separator, not '.')";
		assert methodName == null || methodName.indexOf('.') == -1 && methodName.indexOf('/') == -1 : "not a proper method name";
		assert (methodName == null && methodDesc == null) || (methodName != null && methodDesc != null) : "must have method name and desc nor neither";

		this.className = internalClassName.replace("/", ".");
		this.methodName = methodName;
		this.methodDesc = methodDesc;
	}

	/**
	 * @return a String representation of the content of this method, e.g. for use in exception messages 
	 */
	public final String toContentString() {
		return className + (methodName == null ? "" : "#" + methodName);
	}

	/**
	 * @return this Java element as a method reflection instance or null if not a method
	 * 
	 * @throws InspectionException if this element refers to a non-existing method
	 */
	public Method getJavaMethod() {
		if ( "<init>".equals(methodName) ) {
			return null;
		}
		try {
			final Class<?>[] paramTypes = BytecodeUtil.determineParameterTypes(methodDesc);
			final Class<?> clazz = Class.forName(className);
			return BytecodeUtil.findMethod(clazz, methodName, paramTypes);
		} catch (ClassNotFoundException exc) {
			throw new InspectionException(exc);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + className.hashCode();
		result = prime * result + ((methodDesc == null) ? 0 : methodDesc.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaElement other = (JavaElement) obj;
		if (!className.equals(other.className))
			return false;
		
		// invariant: methodName+methodDesc are either both null or both not null
		if (methodName == null) {
			return other.methodName == null;
		}
		return methodName.equals(other.methodName) && methodDesc.equals(other.methodDesc);  
	}

}
