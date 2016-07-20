package br.com.afirmanet.core.exception;

/**
 * <p>
 * Lançada para indicar um erro genérico em alguma operação de FTP.
 * </p>
 */
public class FtpException extends RuntimeException {

	private static final long serialVersionUID = -2027793229790157485L;

	/**
	 * <p>
	 * Constrói uma FtpException sem mensagem detalhada.
	 * </p>
	 */
	public FtpException() {
		super();
	}

	/**
	 * <p>
	 * Constrói uma FtpException com a mensagem detalhada especificada.
	 * </p>
	 * 
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 */
	public FtpException(String message) {
		super(message);
	}

	/**
	 * <p>
	 * Constrói uma FtpException com a causa especificada e a mensagem detalhada
	 * <tt>(cause==null ? null : cause.toString())</tt>
	 * </p>
	 * 
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public FtpException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Constrói uma FtpException com a causa e mensagem detalhada especificadas.
	 * </p>
	 * <p>
	 * Observar que a mensagem associada com o throwable <code>cause</code> <i>não</i> é automaticamente incorporada na
	 * mensagem detalhada da FtpException.
	 * </p>
	 * 
	 * @param message
	 *        Mensagem que será associada com a exceção.
	 * @param cause
	 *        O Throwable que causou o início da exceção. (Um valor <tt>null</tt> é permitido, e indica que a causa não
	 *        existe ou é desconhecida.)
	 */
	public FtpException(String message, Throwable cause) {
		super(message, cause);
	}
}
