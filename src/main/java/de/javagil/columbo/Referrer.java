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

/** Describes the method which uses the specified element.
 */
public final class Referrer {
	
	/**
	 * Used for line number in declarative code 
	 * because such code does not have line numbers anymore at runtime.
	 */
    public static final Integer NOLINE = null;
    
	public final String className;
    public final String methodName;
    public final String methodDesc;
    public final String sourceFile;
    public final Integer line;

	Referrer(final String className, final String methodName, final String methodDesc, 
				final String sourceFile, final Integer line) {
		
		assert className.indexOf('/') == -1 : "not a proper Java class name";
		assert methodName.indexOf('.') == -1 && methodName.indexOf('/') == -1 : "not a proper method name";  
		
        this.className = className;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.sourceFile = sourceFile; 
        this.line = line;
    }

	@Override
	public String toString() {
		return getClass().getCanonicalName() + "[" + toContentString() + "]";
	}

	/**
	 * @return a textual representation of the contents, omitting the classname 
	 */
	public String toContentString() {
		return className + "#" + methodName + (line != null ? ":" + line : "");
	}
} 