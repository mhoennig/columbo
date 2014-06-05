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

import org.objectweb.asm.Type;

/**
 * Utility class to deal with bytecode details. 
 * 
 * @author michael.hoennig@javagil.de
 */
final class BytecodeUtil {

	// maps "boolean", "int", "short" ... to boolean.class, int.class, short.class ...
	private static final Map<String, Class<?>> PRIMITIVE_NAMES = new HashMap<String, Class<?>>();
	
	// maps B, I, S, ... to boolean.class, int.class, short.class ...
	private static final Map<Character, Class<?>> PRIMITIVE_SYMBOLS = new HashMap<Character, Class<?>>();
	
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

	// utility class
	private BytecodeUtil() {
	}

	/**
	 * Determines the Java name of a given class instance. 
	 * 
	 * @param clazz the class of which we want the name
	 * @return the name of the class as usual in Java (e.g. "java.lang.String", "boolean" or "int[]")
	 */
	public static String getJavaClassName(final Class<?> clazz) {
		if (clazz.isArray()) {
			// we don't want the normal array string representation for primitive types which would be e.g. "[I"
			return getJavaClassName(clazz.getComponentType()) + "[]";
		}
		return clazz.getName();
	}

	/**
	 * @param symbol the internal representation of the primitive type, e.g. 'I' for int. 
	 * @return the class instance of the primitive type, e.g. int.class or null if unknown
	 */
	public static Class<?> getPrimitiveTypeByPrefix(final char symbol) {
		return PRIMITIVE_SYMBOLS.get(symbol);
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
		return paramTypes;
	}
    
   static Class<?> taggedTypeNameToClass(final String taggedTypeNameWithSlashes) {
	   try {
		   Class<?> primitiveType = BytecodeUtil.getPrimitiveTypeByPrefix(taggedTypeNameWithSlashes.charAt(0));
		   if (primitiveType != null) {
			   return primitiveType;   
		   }
		   
	        switch (taggedTypeNameWithSlashes.charAt(0)) {
	        case 'L':
	            return typeNameToClass(taggedTypeNameWithSlashes.substring(1));
	        case '[':
	            return arrayOf(taggedTypeNameToClass(taggedTypeNameWithSlashes.substring(1)));
	
	        default:
	        	return typeNameToClass(taggedTypeNameWithSlashes);
	        }
	   } catch (ClassNotFoundException exc) {
		    throw new InspectionException("could not determine class in type " + taggedTypeNameWithSlashes, exc);
	   }
	}

   static Class<?> classNameToClass(final String typeNameWithDots) {
	   Class<?> primitive = PRIMITIVE_NAMES.get(typeNameWithDots);
	   if (primitive != null) {
		   return primitive;
	   }
	   
	   try {
		   if (typeNameWithDots.endsWith("[]")) {
			   return arrayOf(Class.forName(typeNameWithDots.substring(0, typeNameWithDots.length() - 2)));
		   } else {
			   return Class.forName(typeNameWithDots);
		   }
	   } catch (ClassNotFoundException exc) {
		   throw new InspectionException(exc);
	   }
   }

    static Class<?> typeNameToClass(final String typeNameWithSlashes) throws ClassNotFoundException {
    	Class<?> simpleType = PRIMITIVE_NAMES.get(typeNameWithSlashes);
    	if (simpleType != null) {
    		return simpleType;
    	}
    	
    	String typeNameWithDots = typeNameWithSlashes.replace('/', '.');
    	if (typeNameWithDots.endsWith(";")) {
    		typeNameWithDots = typeNameWithDots.substring(0, typeNameWithDots.length() - 1);
    	}
    	while (typeNameWithDots.endsWith("[]")) {
    		return arrayOf(typeNameToClass(typeNameWithDots.substring(0, typeNameWithDots.length() - 2)));
    	}
    	return Class.forName(typeNameWithDots);
    }
    
	static Class<?> arrayOf(final Class<?> clazz) throws ClassNotFoundException {
		return Class.forName("[" + Type.getDescriptor(clazz).replace('/', '.'));
	}


	private static void definePrimitive(final Class<?> primitive) {
		PRIMITIVE_NAMES.put(primitive.getName(), primitive);
		PRIMITIVE_SYMBOLS.put(Type.getDescriptor(primitive).charAt(0), primitive);
	}

	/**
	 * Finds the method which would be called by given specification.
	 * 
	 * @param clazz the target class of call
	 * @param name name of the method
	 * @param paramTypes parameter types
	 * @return the method which would be called by the given specification or null if none found
	 */
	public static Method findMethod(final Class<?> clazz, final String name, final Class<?>[] paramTypes) {
    	try {
    		// a public method could be found directly
    		return clazz.getMethod(name, paramTypes);
    	} catch (NoSuchMethodException exc1) {
    		// otherwise we check each class in hierarchy separately
    		try {
    			return clazz.getDeclaredMethod(name, paramTypes);
    		} catch (NoSuchMethodException exc2) {
    			if (clazz.getSuperclass() != null) {
    				return findMethod(clazz.getSuperclass(), name, paramTypes);
    			}
    			
    			return null;
    		}
    	}
	}
}
