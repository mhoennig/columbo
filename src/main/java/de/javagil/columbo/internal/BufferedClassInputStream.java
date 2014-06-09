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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import de.javagil.columbo.api.InspectionException;

/**
 * A buffered input stream on the bytecode of a class.
 * 
 * @author michael.hoennig@javagil.de
 */
class BufferedClassInputStream extends BufferedInputStream {

	private final URL source;

	BufferedClassInputStream(final String className) throws ClassNotFoundException {
		this(createClassResource(className));		
	}
	
	private BufferedClassInputStream(final ClassResource classResource) {
		super(classResource.inputStream);
		source = classResource.resource;
	}

	// with this indirection we can avoid calculating the resource twice.
	// the root problem is that we need to pass one part of the result to the superclass constructor
	private static ClassResource createClassResource(final String className) throws ClassNotFoundException {
		Class<?> clazz = getClassLoader().loadClass(className);
		URL resource = clazz.getResource(toClassResourceName(clazz));
		if (resource == null) {
			throw new InspectionException("can't find resource for class " + className + 
							" identified as " + toClassResourceName(clazz)); 
		}
		return new ClassResource(resource, clazz);
	}

	private static String toClassResourceName(final Class<?> clazz) {
		String name = clazz.getName();
		return name.substring(name.lastIndexOf('.') + 1) + ".class"; 
	}

	private static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public URL getResourceURL() {
		return source;
	}

	/**
	 * Used to compute multiple values at once where one of these needs to be passed to the superclass.
	 */
	private static class ClassResource {

		final URL resource;
		final InputStream inputStream;

		ClassResource(final URL resource, final Class<?> clazz) {
			this.resource = resource;
			this.inputStream = clazz.getResourceAsStream(BytecodeUtil.getResourceClassName(clazz));
		}
	}
}
