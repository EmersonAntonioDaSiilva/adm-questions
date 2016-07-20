package br.com.afirmanet.core.util;

///**
// * <p>
// * Classe utilitária para manipulação de digest {@link java.security.MessageDigest}.
// * </p>
// *
// * <p>
// * <b>NOTA:</b> Essa classe apenas implementa algumas funcionalidades inexistentes no projeto <i>commons-codec</i> da
// * Apache Foundation, além de estender sua classe utilitária {@link org.apache.commons.codec.digest.DigestUtils}.
// * </p>
// *
// * <ul>
// * <li><b>md5Hex</b> - calcula o MD5 do arquivo informado</li>
// * </ul>
// *
// * @see org.apache.commons.codec.digest.DigestUtils
// *
// */
////public final class DigestUtils extends org.apache.commons.codec.digest.DigestUtils {
//
//	/**
//	 * <p>
//	 * Classe utilitária não deve ter construtor público ou default.
//	 * </p>
//	 */
//	private DigestUtils() {
//		super();
//	}
//
//	/**
//	 * <p>
//	 * Calcula o MD5 do <code>DataHandler</code> e retorna um valor hexadecimal com 32 caracteres.
//	 * </p>
//	 *
//	 * <p>
//	 * <b>NOTA:</b> Se caso ocorrer uma exceção durante o calculo do MD5 será retornado <code>null</code>.
//	 * <p>
//	 *
//	 * <pre>
//	 * DigestUtils.md5Hex(new DataHandler(new FileDataSource(new File("C:\\teste-md5.xml")))) = 9cd5b1f0cc3e9f7519e7920f78c9e551
//	 * </pre>
//	 *
//	 * @param dataHandler
//	 *        data handler a ser calculado
//	 *
//	 * @return digest MD5 representado por uma string hexadecimal
//	 *
//	 */
//	public static String md5Hex(final DataHandler dataHandler) {
//		try {
//			return org.apache.commons.codec.digest.DigestUtils.md5Hex(dataHandler.getInputStream());
//		} catch (IOException e) {
//			return null;
//		}
//	}
//
//}
