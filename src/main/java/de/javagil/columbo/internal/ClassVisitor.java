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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.EmptyVisitor;

import de.javagil.columbo.api.InspectionException;
import de.javagil.columbo.api.ReferenceVisitor;
import de.javagil.columbo.api.Referrer;
import de.javagil.columbo.api.VisitorContext;


/**
 * This visitor is called by ASM for each class found in the inspected code.
 * It reports all references classes to the reference listener which are used
 * at interface level.
 * 
 * @author michael.hoennig@javagil.de
 */
public class ClassVisitor extends ClassAdapter {

	private final VisitorContext context;
	private final ReferenceVisitor referenceVisitor;
	private final MethodVisitor mv;

	public ClassVisitor(final VisitorContext context, final ReferenceVisitor referenceVisitor) {
        super(new EmptyVisitor());
        this.context = context;
        this.referenceVisitor = referenceVisitor;
		this.mv = new MethodVisitor(context, referenceVisitor);	
    }
    
    @Override
    //CHECKSTYLE:OFF ParameterNumber because I can't shorten parameter list of external methods
    public final void visit(final int version, final int access, final String name, 
    		           final String signature, final String superName, final String[] interfaces) {
        context.enteringClass(name);
        
        Referrer referrer = context.toReferrer();
        Class<?> enteredClass = BytecodeUtil.taggedTypeNameToClass(name);
        scanAnnotations(referrer, enteredClass);
        scanSuperclassAndImplementedInterfaces(referrer, enteredClass);
    }

	//CHECKSTYLE:ON ParameterNumber
    
    @Override
    public void visitSource(final String sourceToVisit, final String debug) {
        context.enteringSource(sourceToVisit);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, 
    								  final String signature, final String[] exceptions) {
    	context.enteringMethod(name, desc);
    	Referrer referrer = context.toReferrer();

		Class<?>[] paramTypes = BytecodeUtil.determineParameterTypes(desc);
		for (Class<?> paramType: paramTypes) {
			referenceVisitor.onClassReference(referrer, paramType);
		}
		
		Method method = referrer.getJavaElement().getJavaMethod();
		if ( method != null ) {
			if ( method.getDeclaringClass().getSuperclass() != null ) {
				checkMethodOverride(referrer, method.getDeclaringClass().getSuperclass(), method);
			}
			for ( Class<?> implementedInterface: method.getDeclaringClass().getInterfaces() ) {
				checkMethodOverride(referrer, implementedInterface, method);
			}
		}
        
        return mv;
    }

    private void checkMethodOverride(Referrer referrer, Class<?> declaringClass, Method method) {
		Method overriddenMethod = BytecodeUtil.findMethod(declaringClass, method.getName(), method.getParameterTypes());
		if ( overriddenMethod != null ) {
			referenceVisitor.onMethodOverride(referrer, overriddenMethod);
		}
	}

	@Override
    public void visitEnd() {
    	context.leavingClass();
    }
    
	public final void inspect(final Set<String> classNamesToInspect) {
        for (String className: classNamesToInspect) {
     		getReferersOfClassName(className);
        }
	}
	
	private void getReferersOfClassName(final String className) {
		try {
			BufferedClassInputStream stream = new BufferedClassInputStream(className);
	        context.enteringResource(stream.getResourceURL());
	        ClassReader reader = new ClassReader(stream);
 	        reader.accept(this, 0);
	        stream.close();
		} catch (ClassNotFoundException exc) {
			throw new InspectionException(exc);
		} catch (IOException exc) {
			throw new InspectionException(exc);
		} finally {
			context.leavingResource();
		}
	}

    private void scanAnnotations(final Referrer referrer, final Class<?> clazz) {
		for ( Annotation annotation: clazz.getAnnotations() ) {
			referenceVisitor.onClassReference(referrer, annotation.annotationType());
		}
	}

	private void scanSuperclassAndImplementedInterfaces(final Referrer referrer, final Class<?> clazz) {
		if ( clazz.getSuperclass() != null ) {
			referenceVisitor.onClassReference(referrer, clazz.getSuperclass());
		}
		for ( Class<?> implementedInterface: clazz.getInterfaces() ) {
			referenceVisitor.onClassReference(referrer, implementedInterface);
		}
	}
}