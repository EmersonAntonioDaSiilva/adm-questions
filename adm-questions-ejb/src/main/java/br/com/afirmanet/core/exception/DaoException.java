package br.com.afirmanet.core.exception;

/**
 * <p>
 * Lançada para indicar um erro genérico em algum DAO.
 * </p>
 */
public class DaoException extends RuntimeException {

	private static final long serialVersionUID = 1072462595077880267L;

	/**
	 * <p>
	 * Constrói uma DaoException sem mensagem detalhada.
	 * </p>
	 */
	public DaoException() {
		super();
	}

	/**
	 * <p>
	 * Constrói uma DaoException com a mensagem detalhada especificada.
	 * </p>
	 * 
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 */
	public DaoException(String message) {
		super(message);
	}

	/**
	 * <p>
	 * Constrói uma DaoException com a causa especificada e a mensagem detalhada
	 * <tt>(cause==null ? null : cause.toString())</tt>
	 * </p>
	 * 
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public DaoException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Constrói uma DaoException com a causa e mensagem detalhada especificadas.
	 * </p>
	 * <p>
	 * Observar que a mensagem associada com o throwable <code>cause</code> <i>não</i> é automaticamente incorporada na
	 * mensagem detalhada da DaoException.
	 * </p>
	 * 
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

}