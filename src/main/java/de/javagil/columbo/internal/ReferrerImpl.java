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

import java.net.URL;

import de.javagil.columbo.api.JavaElement;
import de.javagil.columbo.api.Referrer;

/** Describes the method which uses the specified element.
 */
public final class ReferrerImpl implements Referrer {
	
		/**
	 * Used for line number in declarative code 
	 * because such code does not have line numbers anymore at runtime.
	 */
    public static final Integer NOLINE = null;
    
	public final JavaElement javaElement;
    public final URL resource;
    public final String sourceFile;
    public final Integer line;

	public ReferrerImpl(final JavaElement javaElement,				
				final URL resource, final String sourceFile, final Integer line) {
		assert javaElement != null : "javaElement must not be null";
		
		this.javaElement = javaElement;
		this.resource = resource;
        this.sourceFile = sourceFile; 
        this.line = line;
    }

	@Override
	public String toString() {
		return getClass().getCanonicalName() + "[" + toContentString() + "]";
	}

	@Override
	public String toContentString() {
		return javaElement.toContentString() + (line != null ? ":" + line : "");
	}

	@Override
	public JavaElement getJavaElement() {
		return javaElement;
	}

	@Override
	public URL getResourceURL() {
		return resource;
	}

	@Override
	public String getSourceFile() {
		return sourceFile;
	}

	@Override
	public Integer getLineNo() {
		return line;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + javaElement.hashCode();
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((sourceFile == null) ? 0 : sourceFile.hashCode());
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
		ReferrerImpl other = (ReferrerImpl) obj;
		if (!javaElement.equals(other.javaElement))
			return false;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (sourceFile == null) {
			if (other.sourceFile != null)
				return false;
		} else if (!sourceFile.equals(other.sourceFile))
			return false;
		return true;
	}
} 