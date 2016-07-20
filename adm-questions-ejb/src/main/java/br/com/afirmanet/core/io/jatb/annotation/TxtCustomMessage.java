package br.com.afirmanet.core.io.jatb.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import br.com.afirmanet.core.io.jatb.enumeration.TxtValidatorEnum;

@Retention(RUNTIME)
@Target({ FIELD, METHOD })
@Documented
public @interface TxtCustomMessage {

	TxtValidatorEnum value();

	String errorCode() default "";

	String errorMessage() default "";

}
