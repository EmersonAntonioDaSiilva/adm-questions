package br.com.afirmanet.core.exception;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Lançada para informar uma mensagem de erro de regra de negócio e marcar para rollback.
 * </p>
 */
@javax.ejb.ApplicationException(rollback = true)
public class ApplicationRollbackException extends ApplicationException {

	private static final long serialVersionUID = -8479511652644867458L;

	private Set<String> messages;

	/**
	 * <p>
	 * Constrói uma ApplicationException sem mensagem detalhada.
	 * </p>
	 */
	public ApplicationRollbackException() {
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
	public ApplicationRollbackException(Throwable cause) {
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
	public ApplicationRollbackException(Set<String> messages) {
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
	public ApplicationRollbackException(String message, Object... params) {
		super(message, params);
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
	public ApplicationRollbackException(Throwable cause, String message, Object... params) {
		super(cause, message, cause);
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
	@Override
	public Set<String> getMessages() {
		if (messages == null) {
			messages = new HashSet<>();
		}

		return messages;
	}

}
