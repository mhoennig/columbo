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

import static org.junit.Assert.fail;
import static de.javagil.columbo.internal.Util.areEqual;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.javagil.columbo.api.Referrer;
import de.javagil.columbo.internal.ReferrerImpl;
import de.javagil.columbo.testbed.constructor.SomeClassCallingConstructors;
import de.javagil.columbo.testbed.constructor.SomeClassWithContructor;
import de.javagil.columbo.testbed.deprecated.SomeClassCallingDeprecatedMethods;
import de.javagil.columbo.testbed.deprecated.SomeClassUsingDateConstructor;
import de.javagil.columbo.testbed.deprecated.SomeClassUsingDeprecatedClass;
import de.javagil.columbo.testbed.general.good.SomeCleanClass;

/**
 * Unit test for BytecodeInspector.
 * 
 * @author michael.hoennig@javagil.de 
 */
@SuppressWarnings("javadoc")
public class BytecodeInspectorTest {
	
	private static final String[] BAD_TEST_CLASSES = new String[]{ 
		SomeClassCallingDeprecatedMethods.class.getCanonicalName(),
		SomeClassUsingDateConstructor.class.getCanonicalName(),
		SomeClassUsingDeprecatedClass.class.getCanonicalName()};
	private static final String[] GOOD_TEST_CLASSES = new String[]{ 
		SomeCleanClass.class.getCanonicalName()};

	private static final Referrer[] NO_REFERERS = new Referrer[0];
	
	private BytecodeInspector inspector;
	private List<Referrer> foundReferrers = new ArrayList<Referrer>();

    @Test
    public final void nothingFound() throws Exception {
    	
    	givenBytecodeInspectorForClasses(GOOD_TEST_CLASSES);

    	whenFindCallingMethodsInClassPathUsingMatcher(new ReferenceVisitorAdapter() {
			
    		public void onClassReference(final Referrer referrer, final Class<?> referencedClass) {
    			if (referencedClass.isAnnotationPresent(Deprecated.class)) {
    				foundReferrers.add(referrer);
    			}
    		}
    		
    		public void onMethodCall(final Referrer referrer, final Method referencedMethod) {
    			if (referencedMethod.isAnnotationPresent(Deprecated.class) ||
    				referencedMethod.getDeclaringClass().isAnnotationPresent(Deprecated.class)) {
    				foundReferrers.add(referrer);
    			}
     		}
    		
    	});

    	thenExpectToFind(NO_REFERERS);
    }
    
    @Test
    public final void callersOfDeprecatedMethodsAreFound() throws Exception {
    	
    	givenBytecodeInspectorForClasses(BAD_TEST_CLASSES);

    	whenFindCallingMethodsInClassPathUsingMatcher(new ReferenceVisitorAdapter() {

			public void onClassReference(final Referrer referrer, final Class<?> referencedClass) {
			}
			
			public void onMethodCall(final Referrer referrer, final Method referencedMethod) {
				if (referencedMethod.isAnnotationPresent(Deprecated.class)) {
					foundReferrers.add(referrer);	
				}
			}
				
    	});

    	thenExpectToFind(
    			referer(SomeClassCallingDeprecatedMethods.class, "methodUsingADeprecatedInstanceMethod", 43),
    			referer(SomeClassCallingDeprecatedMethods.class, "methodUsingADeprecatedStaticMethod", 47));
    }
    
    @Test
    public final void methodCallOfDeprecatedClassIsFound() throws Exception {
    	
    	givenBytecodeInspectorForClasses(BAD_TEST_CLASSES);

    	whenFindCallingMethodsInClassPathUsingMatcher(new ReferenceVisitorAdapter() {
    		
    		public void onClassReference(final Referrer referrer, final Class<?> referencedClass) {
    			if (referencedClass.isAnnotationPresent(Deprecated.class)) {
    				foundReferrers.add(referrer);
    			}
    		}
    		
    		public void onMethodCall(final Referrer referrer, final Method referencedMethod) {
    			if (referencedMethod.getDeclaringClass().isAnnotationPresent(Deprecated.class)) {
    				foundReferrers.add(referrer);
    			}
     		}
    	});

    	thenExpectToFind(
    			referer(SomeClassUsingDeprecatedClass.class, "callingMethodOfDeprecatedClass", 43),
    			referer(SomeClassUsingDeprecatedClass.class, "callingMethodOfDeprecatedInterface", null),
    			referer(SomeClassUsingDeprecatedClass.class, "callingMethodOfDeprecatedInterface", 48),
    			referer(SomeClassUsingDeprecatedClass.class, "methodHavingDeprecatedInterfaceAsArgument", null),
    			referer(SomeClassUsingDeprecatedClass.class, "methodUsingDeprecatedInterfaceWithInstanceof", 56));
    }
    
    @Test
    public final void constructorOfDateIsFound() throws Exception {
    	
    	givenBytecodeInspectorForClasses(BAD_TEST_CLASSES);

    	whenFindCallingMethodsInClassPathUsingMatcher(new ReferenceVisitorAdapter() {
			
    		public void onClassReference(final Referrer referrer, final Class<?> referencedClass) {
    			if (referencedClass == java.util.Date.class) {
    				foundReferrers.add(referrer);
    			}
    		}
    		
    		public void onMethodCall(final Referrer referrer, final Method referencedMethod) {
     		}
    	});

    	thenExpectToFind(
    			referer(SomeClassUsingDateConstructor.class, "<clinit>", 40),
    			referer(SomeClassUsingDateConstructor.class, "methodUsingDateConstructor", 43),
    			referer(SomeClassUsingDateConstructor.class, "methodUsingDateConstructor", 44));
    }
    
    @Test
    public final void detectConstructorCallsTest() throws Exception {
    	givenBytecodeInspectorForClasses(new String[]{SomeClassCallingConstructors.class.getCanonicalName()});
    	
    	whenFindCallingMethodsInClassPathUsingMatcher(new ReferenceVisitorAdapter() {
			
    		@Override
    		public void onConstructorCall(final Referrer referrer, final java.lang.reflect.Constructor<?> constructor) {
    			if (constructor.getDeclaringClass() == SomeClassWithContructor.class) {
    				foundReferrers.add(referrer);
    			}
    		};
    	});

    	thenExpectToFind(
    			referer(SomeClassCallingConstructors.class, "<clinit>", 36),
    			referer(SomeClassCallingConstructors.class, "<clinit>", 37), 
    			referer(SomeClassCallingConstructors.class, "<init>", 39),
    			referer(SomeClassCallingConstructors.class, "<init>", 40),
    			referer(SomeClassCallingConstructors.class, "<init>", 50),
    			referer(SomeClassCallingConstructors.class, "<init>", 51),
    			referer(SomeClassCallingConstructors.class, "someMethod", 56), 
    			referer(SomeClassCallingConstructors.class, "someMethod", 57));
    }
    
    // --- test fixture -------------------------------------------------------------------------------------

	private void givenBytecodeInspectorForClasses(final String... testClasses) {
		 inspector = new BytecodeInspector(testClasses);
	}

	private void whenFindCallingMethodsInClassPathUsingMatcher(final ReferenceVisitor matcher) throws Exception {
		inspector.inspect(matcher);
	}

	private void thenExpectToFind(final Referrer... expectedReferers) {
		String expectedButNotFound = findExpectedButNotFound(expectedReferers, foundReferrers);
		String foundButNotExpected = findFoundButNotExpected(foundReferrers, expectedReferers);
		String errors = 
				formatFindingsIfAny("expected but not found:", expectedButNotFound) +
				newLineIfNecessary(expectedButNotFound, foundButNotExpected) +
				formatFindingsIfAny("found but not expected:", foundButNotExpected);
		assertEmpty(errors);
	}

	private void assertEmpty(final String errors) {
		if (errors.length() > 0) {
			fail(errors);
		}
	}

	private String formatFindingsIfAny(final String prefix, final String findings) {
		return findings.length() > 0 ? (prefix + findings) : "";
	}

	private String newLineIfNecessary(final String expectedButNotFound, final String foundButNotExpected) {
		return expectedButNotFound.length() > 0 && foundButNotExpected.length() > 0 ? "\n" : "";
	}

	private String findExpectedButNotFound(final Referrer[] expectedReferrers, final List<Referrer> actualReferrers) {		
		return listThoseWhichAreMissing(expectedReferrers, toArray(actualReferrers));
	}
		
	private String findFoundButNotExpected(final List<Referrer> notExpectedReferrers, final Referrer[] actualReferrers) {
		return listThoseWhichAreMissing(toArray(notExpectedReferrers), actualReferrers);
	}

	private Referrer[] toArray(final List<Referrer> referrers) {
		return referrers.toArray(new Referrer[referrers.size()]);
	}

	private String listThoseWhichAreMissing(final Referrer[] findThese, final Referrer[] inHere) {
		StringBuilder expectedButNotFound = new StringBuilder();
		for (Referrer expectedReferrer: findThese) {
			if (!isContainedIn(expectedReferrer, inHere)) {
				expectedButNotFound.append("\n" + expectedReferrer.toContentString());
			}
		}
		return expectedButNotFound.toString();
	}

	private boolean isContainedIn(final Referrer findThis, final Referrer[] inHere) {
		boolean found = false;
		for (Referrer foundReferrer: inHere) {
			if (foundReferrer.getJavaElement().className.equals(findThis.getJavaElement().className) &&
				foundReferrer.getJavaElement().methodName.equals(findThis.getJavaElement().methodName) &&
				areEqual(foundReferrer.getLineNo(), findThis.getLineNo())) {
				found = true;
				break;
			}
		}
		return found;
	}

	private Referrer referer(final Class<?> clazz, final String method, final Integer lineNo) {
		final String internalClassName = clazz.getName().replace('.', '/');
		return new ReferrerImpl(new JavaElement(internalClassName, method, "()V"), 
					null, clazz.getSimpleName() + ".java", lineNo);
	}
}
