package br.com.afirmanet.core.exception;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Lançada para informar uma mensagem de erro de regra de negócio, como por exemplo: validação e formatação de campos,
 * entre outros.
 * </p>
 */
@javax.ejb.ApplicationException
public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = 5800658504309057308L;

	private Set<String> messages;

	/**
	 * <p>
	 * Constrói uma ApplicationException sem mensagem detalhada.
	 * </p>
	 */
	public ApplicationException() {
		super();
	}

	/**
	 * <p>
	 * Constrói uma ApplicationException com a causa especificada e a mensagem detalhada
	 * <tt>(cause==null ? null : cause.toString())</tt>
	 * </p>
	 *
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public ApplicationException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Constrói uma ApplicationException com as mensagens especificadas.
	 * </p>
	 * <p>
	 * Observar que a mensagem associada com o throwable <code>cause</code> <i>não</i> é automaticamente incorporada na
	 * mensagem detalhada da ApplicationException.
	 * </p>
	 *
	 * @param messages
	 *        Mensagens que serão associadas com a exceção.
	 */
	public ApplicationException(Set<String> messages) {
		super();
		this.messages = messages;
	}

	/**
	 * <p>
	 * Constrói uma ApplicationException com a mensagem detalhada especificada.
	 * </p>
	 *
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 * @param params
	 */
	public ApplicationException(String message, Object... params) {
		super(formatMessage(message, params));
	}

	/**
	 * <p>
	 * Constrói uma ApplicationException com a causa e mensagem detalhada especificadas.
	 * </p>
	 * <p>
	 * Observar que a mensagem associada com o throwable <code>cause</code> <i>não</i> é automaticamente incorporada na
	 * mensagem detalhada da ApplicationException.
	 * </p>
	 *
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 * @param params
	 */
	public ApplicationException(Throwable cause, String message, Object... params) {
		super(formatMessage(message, params), cause);
	}

	/**
	 * <p>
	 * Retorna todas as mensagens associadas a exceção.
	 * </p>
	 * <p>
	 * <b>NOTA:</b> As mensagens retornadas são as mesmas definidas através do contrutor
	 * <code>public ApplicationException(Set messages)</code>.
	 * </p>
	 *
	 * @return Todas as mensagens associadas para a exceção.
	 */
	public Set<String> getMessages() {
		if (messages == null) {
			messages = new HashSet<>();
		}

		return messages;
	}

	private static String formatMessage(String message, Object... params) {
		for (Object param : params) {
			message = message.replaceFirst("\\{\\}", param.toString());
		}

		return message;
	}

}
