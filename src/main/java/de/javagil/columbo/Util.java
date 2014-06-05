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
 * A bunch of convenience methods.
 * 
 * @author michael.hoennig@javagil.de
 */
final class Util {
	
	private Util() {
	}

	/**
	 * Null safe comparison of instances, especially numerics like Integer.
	 * 
	 * @param object1 1st object to compare
	 * @param object2 2ned object to compare
	 * @return true if both null, same instance or both values equal
	 */
	static <T> boolean areEqual(final T object1, final T object2) {
		if (object1 == object2) {
			return true;
		}
		if (object1 == null || object2 == null) {
			return false;
		}
		if (object1.equals(object2)) {
		      return true;
		}
		return false;
	}

	/**
	 * Applies a default value for a string if null or empty.
	 * 
	 * @param string the string to use if it's not null nor empty
	 * @param defaultValue the default value if the string is null or empty
	 * @return the string if not null or empty, defaultValue otherwise
	 */
	static String withDefault(final String string, final String defaultValue) {
		return string == null || string.length() == 0 ? defaultValue : string;
	}
}
