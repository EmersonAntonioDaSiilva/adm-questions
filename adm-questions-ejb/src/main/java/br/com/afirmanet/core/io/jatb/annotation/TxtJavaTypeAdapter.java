package br.com.afirmanet.core.io.jatb.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import br.com.afirmanet.core.io.jatb.adapter.TxtAdapter;

@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface TxtJavaTypeAdapter {

	boolean remove() default false;

	String caracterRemove() default "";

	@SuppressWarnings("rawtypes")
	Class<? extends TxtAdapter> value();

}
