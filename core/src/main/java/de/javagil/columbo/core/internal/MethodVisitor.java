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
package de.javagil.columbo.core.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javassist.bytecode.Opcode;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.commons.EmptyVisitor;

import de.javagil.columbo.core.api.InspectionException;
import de.javagil.columbo.core.api.ReferenceVisitor;
import de.javagil.columbo.core.api.Referrer;
import de.javagil.columbo.core.api.VisitorContext;


/**
 * This visitor is called by ASM for each method found in the inspected code.
 * It reports all references classes to the reference listener which are used
 * at implementation level.
 * 
 * @author michael.hoennig@javagil.de
 */
class MethodVisitor extends MethodAdapter {

	private final ReferenceVisitor referenceVisitor;
    private final VisitorContext context;

	MethodVisitor(final VisitorContext context, final ReferenceVisitor referenceVisitor) {
        super(new EmptyVisitor());
        this.referenceVisitor = referenceVisitor;
        this.context = context;
    }

	@Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
    	Referrer referrer = context.toReferrer();
    	
    	Class<?> clazz = BytecodeUtil.taggedTypeNameToClass(owner);
    	referenceVisitor.onClassReference(referrer, rawType(clazz));

        if (opcode == Opcode.INVOKESPECIAL && isConstructor(name)) {
        	onConstructorCall(referrer, clazz, name, desc);
        } else if (opcode == Opcode.INVOKESPECIAL || 
        		   opcode == Opcode.INVOKEINTERFACE || 
        		   opcode == Opcode.INVOKEVIRTUAL || 
        		   opcode == Opcode.INVOKESTATIC) {
            onMethodCall(referrer, clazz, name, desc);            
        }
    }

	private boolean isConstructor(final String name) {
		return "<init>".equals(name);
	}

	private void onConstructorCall(final Referrer referrer, final Class<?> clazz, final String name, final String desc) {
		final Constructor<?> constructor = findConstructor(rawType(clazz), desc);
		referenceVisitor.onConstructorCall(referrer, constructor);
		
		notifyParameterTypes(referrer, constructor.getParameterTypes());
	}

	private void onMethodCall(final Referrer referrer, final Class<?> clazz, final String name, final String desc) {
		final Method method = findMethod(rawType(clazz), name, desc);
		if (method == null) {
			// TODO it's ugly to calculate paramTypes in findMethod as well as here
			// needs refactoring and test coverage (this is just a hotfix)
			referenceVisitor.onMethodNotFound(clazz, name, BytecodeUtil.determineParameterTypes(desc));
		} else {
			referenceVisitor.onMethodCall(referrer, method);
			referenceVisitor.onClassReference(referrer, rawType(method.getReturnType()));
			notifyParameterTypes(referrer, method.getParameterTypes());
		}
	}

	private void notifyParameterTypes(final Referrer referrer, final Class<?>[] parameterTypes) {
	    for (Class<?> paramType: parameterTypes) {
	    	referenceVisitor.onClassReference(referrer, rawType(paramType));
	    }
	}

    private Class<?> rawType(final Class<?> type) {
    	if (!type.isArray()) {
    		return type;
    	}
    	return rawType(type.getComponentType());
	}

	Constructor<?> findConstructor(final Class<?> clazz, final String desc) {
		final Class<?>[] paramTypes = BytecodeUtil.determineParameterTypes(desc);
		final Constructor<?> constructor = BytecodeUtil.findConstructor(clazz, paramTypes);
		if (constructor == null) {
			// might throw {@link InspectorException}, but not necessarily
			referenceVisitor.onConstructorNotFound(clazz, paramTypes);
		} 
		return constructor;
	}


	Method findMethod(final Class<?> clazz, final String name, final String desc) {

		final Class<?>[] paramTypes = BytecodeUtil.determineParameterTypes(desc);
		final Method method = BytecodeUtil.findMethod(clazz, name, paramTypes);
		if (method == null) {
			// might throw {@link InspectorException}, but not necessarily
			referenceVisitor.onMethodNotFound(clazz, name, paramTypes);
		}
		return method;
	}

	@Override
	public void visitTypeInsn(final int opcode, final String type) {
		try  {
			if (opcode == Opcode.NEW || opcode == Opcode.INSTANCEOF) { 
				Class<?> clazz = BytecodeUtil.typeNameToClass(type);
				referenceVisitor.onClassReference(context.toReferrer(), clazz); 
			}
		} catch (ClassNotFoundException exc) {
        	throw new InspectionException(exc);
		}
    	super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        context.inspectingLineNumber(line);
    }
    
    @Override
    public void visitEnd() {
    	context.leavingMethod();
    }

}