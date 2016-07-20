package br.com.afirmanet.core.exception;

/**
 * <p>
 * Lançada para indicar um erro genérico por algum problema de sistema/ambiente, como por exemplo: banco de dados
 * inoperante, tráfego de rede lento, entre outros.
 * </p>
 */
public class SystemException extends RuntimeException {

	private static final long serialVersionUID = -8479511652644867458L;

	/**
	 * <p>
	 * Constrói uma SystemException sem mensagem detalhada.
	 * </p>
	 */
	public SystemException() {
		super();
	}

	/**
	 * <p>
	 * Constrói uma SystemException com a mensagem detalhada especificada.
	 * </p>
	 *
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 */
	public SystemException(String message) {
		super(message);
	}

	/**
	 * <p>
	 * Constrói uma SystemException com a causa especificada e a mensagem detalhada
	 * <tt>(cause==null ? null : cause.toString())</tt>
	 * </p>
	 *
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public SystemException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Constrói uma SystemException com a causa e mensagem detalhada especificadas.
	 * </p>
	 * <p>
	 * Observar que a mensagem associada com o throwable <code>cause</code> <i>não</i> é automaticamente incorporada na
	 * mensagem detalhada da SystemException.
	 * </p>
	 *
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public SystemException(String format, Object... args) {
		super(String.format(format, args));
	}

}
