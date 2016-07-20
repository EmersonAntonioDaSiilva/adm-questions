package br.com.afirmanet.core.util;

/**
 * <p>
 * Classe utilitária para manipulação de <code>Integer</code>.
 * </p>
 */
public final class IntegerUtils extends NumberUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private IntegerUtils() {
		super();
	}

	/**
	 * <p>
	 * Faz um parse da <code>String</code> para produzir um valor numérico. O parse é não lenient, ou seja, a entrada
	 * deve corresponder a um valor numérico.
	 * </p>
	 *
	 * <pre>
	 * IntegerUtils.parse("125635")          = 125635
	 * IntegerUtils.parse("154989,215")      = 154989215
	 * IntegerUtils.parse("1@54.9$8#9,21A5") = 154989215
	 * </pre>
	 *
	 * @param source
	 *        uma <code>String</code> que representa o valor numérico
	 *
	 * @return um <code>Integer</code> criado a partir da <code>String</code>
	 *
	 * @throws IllegalArgumentException
	 *         se a <code>String</code> informada for <code>null</code>, vazio <code>("")</code> ou espaço em branco
	 *         <code>(" ")</code>
	 */
	public static Integer parse(final String source) {
		String digits = getDigits(source);
		return Integer.parseInt(digits);
	}

	/**
	 * <p>
	 * Faz um parse da string para produzir um valor numérico. Se o parse for lenient, então ao ocorrer uma exceção
	 * durante o parse será retornado <code>null</code>.
	 * </p>
	 *
	 * <pre>
	 * IntegerUtils.parse("125635", true)               = 125635
	 * IntegerUtils.parse("154989,215", true)           = 154989215
	 * IntegerUtils.parse("1@54.9$8#9,21A5", true)      = 154989215
	 * IntegerUtils.parse("    1@54.9$8#9,21A5", true)  = 154989215
	 * IntegerUtils.parse("", true)                     = null
	 * IntegerUtils.parse(" ", true)                    = null
	 * IntegerUtils.parse(null, true)                   = null
	 * </pre>
	 *
	 * @param source
	 *        uma <code>String</code> que representa o valor numérico
	 * @param lenient
	 *        um <code>boolean</code> que indica se o parse vai ser realizado de forma lenient ou não
	 *
	 * @return um <code>Integer</code> criado a partir da <code>String</code>
	 *
	 * @exception IllegalArgumentException
	 *            se o texto informado for <code>null</code>, vazio <code>("")</code> ou espaço em branco
	 *            <code>(" ")</code> quando o lenient for <code>false</code>
	 */
	public static Integer parse(final String source, final boolean lenient) {
		Integer number = null;

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
	 * Retorna o <code>Integer</code> informado ou <code>0</code> caso o valor informado seja <code>null</code>.
	 * </p>
	 *
	 * <pre>
	 * IntegerUtils.defaultInteger(null)                     = 0
	 * IntegerUtils.defaultInteger(new Integer("188900099")) = 188900099
	 * </pre>
	 *
	 * @param number
	 *        valor a ser analisado
	 *
	 * @return o <code>Integer</code> informado ou <code>0</code> se for <code>null</code>
	 */
	public static Integer defaultInteger(final Integer number) {
		return number == null ? Integer.valueOf(0) : number;
	}

	public static boolean isZero(final Integer number) {
		return isEqual(number, 0);
	}

	public static boolean isEqual(final Integer number1, final Integer number2) {
		return number1.compareTo(number2) == 0;
	}

	public static boolean isNotEqual(final Integer number1, final Integer number2) {
		return number1.compareTo(number2) != 0;
	}

	public static boolean isGreater(final Integer number1, final Integer number2) {
		return number1.compareTo(number2) > 0;
	}

	public static boolean isLess(final Integer number1, final Integer number2) {
		return number1.compareTo(number2) < 0;
	}

	public static boolean isGreaterThanZero(final Integer number) {
		if (number == null) {
			return false;
		}

		return number.compareTo(0) > 0;
	}

	public static boolean isGreaterOrEqual(final Integer number1, final Integer number2) {
		return number1.compareTo(number2) >= 0;
	}

	public static boolean isLessOrEqual(final Integer number1, final Integer number2) {
		return number1.compareTo(number2) <= 0;
	}

}
