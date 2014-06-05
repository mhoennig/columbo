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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Scans the classpath for usages of classes of a given package and collects a filtered list. 
 * E.g. all usages of @Deprecated elements could be found, who have an extra annotation 
 * which determines that a deprecation test should actually fail. Or we could test whether 
 * classes, annotated with @Internal are only used local to their own package hierarchy 
 * (however you define the local). 
 * 
 * @author michael.hoennig@javagil.de 
 */
public class BytecodeInspector {

	private final Set<String> classNamesToInspect;
	private final VisitorContext context = new VisitorContext();
	
	/**
     * Creates an instance which searches for all elements within the given package name.
     * Attention: Currently you can only specify which elements are used, not which elements to search through. 
     * 
     * @param classNamesToInspect set of qualified class names to load and inspect
     */
    public BytecodeInspector(final Set<String> classNamesToInspect) {
    	this.classNamesToInspect = classNamesToInspect;
	}

    /**
     * Creates an instance which searches for all elements within the given package name.
     * Attention: Currently you can only specify which elements are used, not which elements to search through. 
     * 
     * @param classNamesToInspect qualified class names to load and inspect
     */
    public BytecodeInspector(final String... classNamesToInspect) {
    	this(new HashSet<String>(Arrays.asList(classNamesToInspect)));
	}

	/**
     * Searches the classpath for elements belonging classes of the package specified in the constructor 
     * which match the given {@link ReferenceVisitor}. 
     * 
     * @param referenceVisitor callback interface to determine which elements to be included in the result
     * @throws InspectionException on classloading or bytecode reading problems
     */
    public final void inspect(final ReferenceVisitor referenceVisitor) throws InspectionException {
		new ClassVisitor(context, referenceVisitor).inspect(classNamesToInspect);        
    }

}