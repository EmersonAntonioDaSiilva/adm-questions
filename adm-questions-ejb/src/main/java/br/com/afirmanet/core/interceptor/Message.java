package br.com.afirmanet.core.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * Adiciona mensagem de erro definida na ApplicationException. Para outras exceções, loga e adiciona mensagem de erro
 * genérica. <br>
 * <br>
 * Usar a anotação ao invés do código:
 *
 * <pre>
 * try {
 *    ...
 *
 * } catch (ApplicationException e) {
 *    addErrorMessage(e);
 * } catch (Exception e) {
 *    addFatalErrorMessage(e);
 * }
 * </pre>
 */
@InterceptorBinding
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Message {
	//
}
