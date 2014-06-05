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

package de.javagil.columbo;

/**
 * Keeps track of the current context (source/class/method/line) the visitors are scanning. 
 * 
 * @author  michael.hoennig@javagil.de
 */
class VisitorContext {
	private String currentSource;
	private String currentClassName;
	private String currentMethodName;
	private String currentMethodDesc;
	private Integer currentLineNumber;

	String getCurrentSource() {
		return currentSource;
	}

	String getCurrentMethodName() {
		return currentMethodName;
	}

	String getCurrentMethodDesc() {
		return currentMethodDesc;
	}

	Integer getCurrentLineNumber() {
		return currentLineNumber;
	}

	String getCurrentClassName() {
		return currentClassName;
	}

	public void enteringClass(final String name) {
		assert currentClassName == null : "multiple calls to enteringClass without leavingClass";
		
		currentClassName = name;
		currentMethodName = null;
		currentMethodDesc = null;
		currentLineNumber = null;
	}

	void enteringSource(final String sourceToVisit) {
		assert currentClassName != null : "enteringSource without preceeding enteringClass";
		
		currentSource = sourceToVisit;
		// do not null the other fields as enteringSource is called AFTER enteringClass
	}
	
	void enteringMethod(final String name, final String desc) {
		assert currentClassName != null && currentSource != null : "enteringMethod without enteringSource and/or enteringClass";
		
        currentMethodName = name;
        currentMethodDesc = desc;
        currentLineNumber = null;
	}

	void inspectingLineNumber(final Integer lineNo) {
		currentLineNumber = lineNo;
	}

	void leavingMethod() {
		assert currentMethodName != null && currentMethodDesc != null : "leavingMethod while not inside of a method";
		
		currentMethodName = null;
		currentMethodDesc = null;
		currentLineNumber = null;
	}

	public void leavingClass() {
		assert currentClassName != null : "leavingClass while not inside of a class";
		
		currentClassName = null;
	}

	Referrer toReferrer() {
		// TODO move replace(...) to 'setter' or caller
		return new Referrer(currentClassName.replace("/", "."), currentMethodName, currentMethodDesc, 
				currentSource, currentLineNumber);
	}

}