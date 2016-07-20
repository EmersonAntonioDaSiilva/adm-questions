package br.com.afirmanet.core.exception;

/**
 * <p>
 * Lançada para indicar um erro genérico em alguma operação de CRUD.
 * </p>
 */
public class CrudException extends RuntimeException {

	private static final long serialVersionUID = -3148447615626817101L;

	/**
	 * <p>
	 * Constrói uma CrudException sem mensagem detalhada.
	 * </p>
	 */
	public CrudException() {
		super();
	}

	/**
	 * <p>
	 * Constrói uma CrudException com a mensagem detalhada especificada.
	 * </p>
	 * 
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 */
	public CrudException(String message) {
		super(message);
	}

	/**
	 * <p>
	 * Constrói uma CrudException com a causa especificada e a mensagem detalhada
	 * <tt>(cause==null ? null : cause.toString())</tt>
	 * </p>
	 * 
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public CrudException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Constrói uma CrudException com a causa e mensagem detalhada especificadas.
	 * </p>
	 * <p>
	 * Observar que a mensagem associada com o throwable <code>cause</code> <i>não</i> é automaticamente incorporada na
	 * mensagem detalhada da CrudException.
	 * </p>
	 * 
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public CrudException(String message, Throwable cause) {
		super(message, cause);
	}

}