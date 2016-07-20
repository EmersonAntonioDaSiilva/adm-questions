package br.com.afirmanet.core.util;

/**
 * <p>
 * Classe utilitária para manipulação de <code>java.lang.Long</code>.
 * </p>
 */
public final class LongUtils extends NumberUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private LongUtils() {
		super();
	}

	/**
	 * <p>
	 * Faz um parse da <code>String</code> para produzir um valor numérico. O parse é não lenient, ou seja, a entrada
	 * deve corresponder a um valor numérico.
	 * </p>
	 *
	 * <pre>
	 * LongUtils.parse("125635")          = 125635
	 * LongUtils.parse("154989,215")      = 154989215
	 * LongUtils.parse("1@54.9$8#9,21A5") = 154989215
	 * </pre>
	 *
	 * @param source
	 *        uma <code>String</code> que representa o valor numérico
	 *
	 * @return um <code>Long</code> criado a partir da <code>String</code>
	 *
	 * @throws IllegalArgumentException
	 *         se a <code>String</code> informada for <code>null</code>, vazio <code>("")</code> ou espaço em branco
	 *         <code>(" ")</code>
	 *
	 */
	public static Long parse(final String source) {
		String digits = getDigits(source);
		return Long.parseLong(digits);
	}

	/**
	 * <p>
	 * Faz um parse da <code>String</code> para produzir um valor numérico. Se o parse for lenient, então ao ocorrer uma
	 * exceção durante o parse será retornado <code>null</code>.
	 * </p>
	 *
	 * <pre>
	 * LongUtils.parse("125635", true)               = 125635
	 * LongUtils.parse("154989,215", true)           = 154989215
	 * LongUtils.parse("1@54.9$8#9,21A5", true)      = 154989215
	 * LongUtils.parse("    1@54.9$8#9,21A5", true)  = 154989215
	 * LongUtils.parse("", true)                     = null
	 * LongUtils.parse(" ", true)                    = null
	 * LongUtils.parse(null, true)                   = null
	 * </pre>
	 *
	 * @param source
	 *        uma <code>String</code> que representa o valor numérico
	 * @param lenient
	 *        um <code>boolean</code> que indica se o parse vai ser realizado de forma lenient ou não
	 *
	 * @return um <code>Long</code> criado a partir da <code>String</code>
	 *
	 * @exception IllegalArgumentException
	 *            se o texto informado for <code>null</code>, vazio <code>("")</code> ou espaço em branco
	 *            <code>(" ")</code> quando o lenient for <code>false</code>
	 */
	public static Long parse(final String source, final boolean lenient) {
		Long number = null;

		try {
			number = parse(source);

		} catch (Exception e) {
			if (!lenient) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}

		return number;
	}

	/**
	 * <p>
	 * Retorna o <code>Long</code> informado ou <code>0L</code> caso o valor informado seja <code>null</code>.
	 * </p>
	 *
	 * <pre>
	 * LongUtils.defaultLong(null)                  = 0
	 * LongUtils.defaultLong(new Long("188900099")) = 188900099
	 * </pre>
	 *
	 * @param number
	 *        valor a ser analisado
	 *
	 * @return o <code>Long</code> informado ou <code>0L</code> se for <code>null</code>
	 *
	 */
	public static Long defaultLong(final Long number) {
		return number == null ? Long.valueOf(0) : number;
	}

}
