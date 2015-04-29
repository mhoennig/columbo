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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javassist.bytecode.Opcode;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.commons.EmptyVisitor;

import de.javagil.columbo.api.InspectionException;
import de.javagil.columbo.api.ReferenceVisitor;
import de.javagil.columbo.api.Referrer;
import de.javagil.columbo.api.VisitorContext;


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
		if ( clazz.isPrimitive() ) {
			return;
		}
    	referenceVisitor.onClassReference(referrer, BytecodeUtil.rawType(clazz));

        if (opcode == Opcode.INVOKESPECIAL && isConstructor(name)) {
        	onConstructorCall(referrer, clazz, name, desc);
        } else if (opcode == Opcode.INVOKESPECIAL || 
        		   opcode == Opcode.INVOKEINTERFACE || 
        		   opcode == Opcode.INVOKEVIRTUAL || 
        		   opcode == Opcode.INVOKESTATIC) {
            onMethodCall(referrer, clazz, name, desc);            
        }
    }
	
	@Override
	public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
		Referrer referrer = context.toReferrer();
    	
    	Class<?> clazz = BytecodeUtil.taggedTypeNameToClass(owner);
    	referenceVisitor.onClassReference(referrer, BytecodeUtil.rawType(clazz));

        if (opcode == Opcode.GETSTATIC || opcode == Opcode.PUTSTATIC || opcode == Opcode.GETFIELD || opcode == Opcode.PUTFIELD) {
            onFieldAccess(referrer, clazz, name);            
        } else {
        	throw new InspectionException("opcode " + opcode + " not implemented for field instruction");
        }
	}

	private boolean isConstructor(final String name) {
		return "<init>".equals(name);
	}

	private void onConstructorCall(final Referrer referrer, final Class<?> clazz, final String name, final String desc) {
		final Constructor<?> constructor = findConstructor(BytecodeUtil.rawType(clazz), desc);
		if ( constructor != null ) {
			referenceVisitor.onConstructorCall(referrer, constructor);
			notifyParameterTypes(referrer, constructor.getParameterTypes());
		}
	}

	private void onMethodCall(final Referrer referrer, final Class<?> clazz, final String name, final String desc) {
		final Method method = findMethod(BytecodeUtil.rawType(clazz), name, desc);
		if (method != null) {
			referenceVisitor.onMethodCall(referrer, method);
			referenceVisitor.onClassReference(referrer, BytecodeUtil.rawType(method.getReturnType()));
			notifyParameterTypes(referrer, method.getParameterTypes());
		}
	}

	private void onFieldAccess(final Referrer referrer, final Class<?> clazz, final String name) {
		final Field field = findField(BytecodeUtil.rawType(clazz), name);
		if (field != null) {
			referenceVisitor.onFieldAccess(referrer, field);
			referenceVisitor.onClassReference(referrer, BytecodeUtil.rawType(field.getType()));
		}
	}

	private void notifyParameterTypes(final Referrer referrer, final Class<?>[] parameterTypes) {
	    for (Class<?> paramType: parameterTypes) {
	    	referenceVisitor.onClassReference(referrer, BytecodeUtil.rawType(paramType));
	    }
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

	Field findField(final Class<?> clazz, final String name) {
		final Field field = BytecodeUtil.findField(clazz, name);
		if (field == null) {
			// might throw {@link InspectorException}, but not necessarily
			referenceVisitor.onFieldNotFound(clazz, name);
		}
		return field;
	}

	@Override
	public void visitTypeInsn(final int opcode, final String type) {
		if (opcode == Opcode.NEW || opcode == Opcode.INSTANCEOF) {
			Class<?> clazz = BytecodeUtil.taggedTypeNameToClass(type);
			referenceVisitor.onClassReference(context.toReferrer(), clazz);
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