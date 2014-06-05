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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.bytecode.Opcode;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;


/**
 * This visitor is called by ASM for each method found in the inspected code.
 * It reports all references classes to the reference listener which are used
 * at implementation level.
 * 
 * @author michael.hoennig@javagil.de
 */
class MethodVisitor extends MethodAdapter {

	// maps "boolean", "int", "short" ... to boolean.class, int.class, short.class ...
	private static final Map<String, Class<?>> PRIMITIVES = new HashMap<String, Class<?>>();
	
	// maps B, I, S, ... to boolean.class, int.class, short.class ...
	private static final Map<Character, Class<?>> PREFIXES = new HashMap<Character, Class<?>>();
	
	static {
		definePrimitive(boolean.class);
		definePrimitive(char.class);
		definePrimitive(byte.class);
		definePrimitive(short.class);
		definePrimitive(int.class);
		definePrimitive(long.class);
		definePrimitive(double.class);
		definePrimitive(float.class);
	}

	private final ReferenceVisitor referenceVisitor;
    private final VisitorContext context;

	MethodVisitor(final VisitorContext context, final ReferenceVisitor referenceVisitor) {
        super(new EmptyVisitor());
        this.referenceVisitor = referenceVisitor;
        this.context = context;
    }

	@Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        if (opcode == Opcode.INVOKEINTERFACE || opcode == Opcode.INVOKEVIRTUAL || opcode == Opcode.INVOKESTATIC) {
        	Referrer referrer = context.toReferrer();
        	
        	Class<?> clazz = taggedTypeNameToClass(owner);
        	referenceVisitor.onClassReference(referrer, clazz);
        	
            Class<?>[] parameterTypes = determineParameterTypes(desc);
            if (parameterTypes != null) { // TODO check if we can use empty array  
	            for (Class<?> paramType: parameterTypes) {
	            	referenceVisitor.onClassReference(referrer, paramType);
	            }
        	}
            
            final Method method = findMethod(clazz, name, desc);
            referenceVisitor.onMethodReference(referrer, method);
           	referenceVisitor.onClassReference(referrer, method.getReturnType());
        }
    }

    static Method findMethod(final Class<?> clazz, final String name, final String desc) {
    	try {
			return clazz.getDeclaredMethod(name, determineParameterTypes(desc));
		} catch (NoSuchMethodException exc) {
			if (clazz.getSuperclass() != null && clazz.getSuperclass() != java.lang.Object.class) {
				return findMethod(clazz.getSuperclass(), name, desc);
			} 
			
			throw new InspectionException(exc);
		}
	}

	@Override
	public void visitTypeInsn(final int opcode, final String type) {
		try  {
			if (opcode == Opcode.NEW || opcode == Opcode.INSTANCEOF) { 
				Class<?> clazz = typeNameToClass(type);
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
    
	static Class<?>[] determineParameterTypes(final String desc) {
		Type[] argTypes = Type.getArgumentTypes(desc);
		
		List<Class<?>> paramTypeList = new ArrayList<Class<?>>();
		for (int n = 0; n < argTypes.length; ++n) {
			if (argTypes[n] != null) {
				paramTypeList.add(taggedTypeNameToClass(argTypes[n].getClassName()));
			}
		}
		Class<?>[] paramTypes = paramTypeList.toArray(new Class<?>[paramTypeList.size()]);
		// TODO check if we can use empty array
		return paramTypes.length == 0 ? null : paramTypes;
	}
    
   static Class<?> taggedTypeNameToClass(final String taggedTypeNameWithSlashes) {
	   try {
		   Class<?> primitiveType = PREFIXES.get(taggedTypeNameWithSlashes.charAt(0));
		   if (primitiveType != null) {
			   return primitiveType;   
		   }
		   
	        switch (taggedTypeNameWithSlashes.charAt(0)) {
	        case 'L':
	            return typeNameToClass(taggedTypeNameWithSlashes.substring(1));
	        case '[':
	            return taggedTypeNameToClass(taggedTypeNameWithSlashes.substring(1));
	
	        default:
	        	return typeNameToClass(taggedTypeNameWithSlashes);
	        }
	   } catch (ClassNotFoundException exc) {
		    throw new InspectionException("could not determine class in type " + taggedTypeNameWithSlashes, exc);
	   }
	}

    private static Class<?> typeNameToClass(final String typeNameWithSlashes) throws ClassNotFoundException {
    	Class<?> simpleType = PRIMITIVES.get(typeNameWithSlashes);
    	if (simpleType != null) {
    		return simpleType;
    	}
    	
    	String typeNameWithDots = typeNameWithSlashes.replace('/', '.');
    	if (typeNameWithDots.endsWith(";")) {
    		typeNameWithDots = typeNameWithDots.substring(0, typeNameWithDots.length() - 1);
    	}
    	return Class.forName(typeNameWithDots);
    }
    
	private static void definePrimitive(final Class<?> primitive) {
		PRIMITIVES.put(primitive.getName(), primitive);
		PREFIXES.put(Type.getDescriptor(primitive).charAt(0), primitive);
	}

}