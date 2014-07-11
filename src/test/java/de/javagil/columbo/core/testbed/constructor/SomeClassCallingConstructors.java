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

package de.javagil.columbo.core.testbed.constructor;

/**
 * Just some class whose constructor we want to find in a test.
 * 
 * @author michael.hoennig@javagil.de
 */
public class SomeClassCallingConstructors {
	
	public static final SomeClassWithContructor CLASS_INIT0 = new SomeClassWithContructor();
	public static final SomeClassWithContructor CLASS_INIT1 = new SomeClassWithContructor(13);

	public final SomeClassWithContructor instanceInit0 = new SomeClassWithContructor();
	public final SomeClassWithContructor instanceInit1 = new SomeClassWithContructor(13);

	public final SomeClassWithContructor constructor0;
	public final SomeClassWithContructor constructor1;
	
	private SomeClassWithContructor method0;
	private SomeClassWithContructor method1;

	public SomeClassCallingConstructors() {
		// calls from constructor
		this.constructor0 = new SomeClassWithContructor();
		this.constructor1 = new SomeClassWithContructor(99);
	}
	
	// calls from a method
	final void someMethod() {
		this.method0 = new SomeClassWithContructor();
		this.method1 = new SomeClassWithContructor(99);
	}

	public final int nothingShouldBeFoundHereAsThereIsNotConstructorCall() {
		return method0.getDummy();
	}

	public final int nothingShouldBeFoundHereAsThereIsNotConstructorCall(final SomeClassWithContructor ref) {
		if (ref != null) {
			return ref.getDummy();
		} else {
			return method1.getDummy();
		}
	}
	
}
