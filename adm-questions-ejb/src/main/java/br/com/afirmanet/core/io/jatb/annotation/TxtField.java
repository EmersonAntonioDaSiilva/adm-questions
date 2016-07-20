package br.com.afirmanet.core.io.jatb.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD, METHOD })
@Documented
public @interface TxtField {

	int startChar();

	int endChar();

	boolean required() default false;

	String defaultValue() default "";

	String[] acceptedValues() default {};

	char padChar() default '0';

	boolean forceValidator() default false;

	boolean stopProcessingOnValidatorError() default false;

	TxtCustomMessage[] customMessages() default {};

}
