package br.com.afirmanet.core.exception;

/**
 * <p>
 * Lançada para indicar um erro no repositório de arquivos.
 * </p>
 */
public class FileRepositoryException extends RuntimeException {

	private static final long serialVersionUID = -6760706341215706124L;

	/**
	 * <p>
	 * Constrói uma FileRepositoryException sem mensagem detalhada.
	 * </p>
	 */
	public FileRepositoryException() {
		super();
	}

	/**
	 * <p>
	 * Constrói uma FileRepositoryException com a mensagem detalhada especificada.
	 * </p>
	 * 
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 */
	public FileRepositoryException(String message) {
		super(message);
	}

	/**
	 * <p>
	 * Constrói uma FileRepositoryException com a causa especificada e a mensagem detalhada
	 * <tt>(cause==null ? null : cause.toString())</tt>
	 * </p>
	 * 
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public FileRepositoryException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Constrói uma FileRepositoryException com a causa e mensagem detalhada especificadas.
	 * </p>
	 * <p>
	 * Observar que a mensagem associada com o throwable <code>cause</code> <i>não</i> é automaticamente incorporada na
	 * mensagem detalhada da FileRepositoryException.
	 * </p>
	 * 
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public FileRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

}