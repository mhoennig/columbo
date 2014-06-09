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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.net.URL;
import java.net.MalformedURLException;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 * Searches classpath for all classes by specific criteria.
 * 
 * @author michael.hoennig@javagil.de
 */
public class ClassFinder {

	private static final boolean INCLUDE_JAVA_LANG_OBJECT = false; // means do not exclude!
	private final Reflections reflections;

	/**
	 * Creates a new instance which finds classes recursively within a given package.
	 * 
	 * @param packageName the name of the package (subpackages will be included)
	 */
	public ClassFinder(final String packageName) {
		
		reflections = new Reflections(new ConfigurationBuilder()
		    .setUrls(getURLs(packageName))
		    .setScanners(new SubTypesScanner(INCLUDE_JAVA_LANG_OBJECT), new ResourcesScanner())
		    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));
	}

	private static Set<URL> getURLs(final String packageName) {
		final Set<URL> urls = new HashSet<URL>();
		final String packagePath = packageName.replace('.', '/');
		final int packageLen = packagePath.length();
		final Set<URL> foundUrls = ClasspathHelper.forPackage(packageName);
		for (URL url : foundUrls) {
		    final String urlStr = url.toString();
		    if (urlStr.endsWith(packagePath)) {
			try {
			    final URL newUrl = new URL(urlStr.substring(0, urlStr.length() - packageLen));
			    urls.add(newUrl);
			} catch (final MalformedURLException ex) {
			    throw new RuntimeException(ex);
			}
		    } else {
		    	urls.add(url);
		    }
		}
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
		//classLoadersList.add(getClass().getClassLoader());
		urls.addAll(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])));
		
		return urls;
	    }
	
	/**
	 * Searches the classpath for all classes names within the given package hierarchy.
	 * 
	 * @return all classes names as specified
	 */
	public final Set<String> findAllClassNames() {
	    Set<String> names = new HashSet<String>();
	    
		for (Class<? extends Object> clazz: reflections.getSubTypesOf(java.lang.Object.class)) {
			String clazzName = clazz.getName();
			if (clazzName != null) {
				names.add(clazzName);
			} 
		}

	    return names;
	}

}
