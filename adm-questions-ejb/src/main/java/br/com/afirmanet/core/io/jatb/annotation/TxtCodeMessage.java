package br.com.afirmanet.core.io.jatb.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import br.com.afirmanet.core.io.jatb.validator.TxtValidator;

@Retention(RUNTIME)
@Target({ FIELD, METHOD })
@Documented
public @interface TxtCodeMessage {

	String param() default "";

	@SuppressWarnings("rawtypes")
	Class<? extends TxtValidator> value();

}
