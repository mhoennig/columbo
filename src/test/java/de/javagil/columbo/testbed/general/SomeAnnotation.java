package de.javagil.columbo.testbed.general;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Serves as a test annotation for bytecode inspection.
 * 
 * @author michael.hoennig@javagil.de
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SomeAnnotation {

}
