package br.com.afirmanet.core.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import br.com.afirmanet.core.constant.LocaleConstants;
import br.com.afirmanet.core.exception.ApplicationException;

/**
 * <p>
 * Classe utilitária para manipulação de <code>java.math.BigDecimal</code>.
 * </p>
 *
 * <ul>
 * <li><b>parse</b> - realiza um parse de uma <code>String</code> para um <code>BigDecimal</code></li>
 * <li><b>defaultBigDecimal</b> - caso o <code>BigDecimal</code> seja <code>null</code> é retornado 0</li>
 * <li><b>format</b> - formata o decimal com a máscara default: {@value #DEFAULT_DECIMAL_PATTERN} ou com a máscara
 * informada</li>
 * </ul>
 *
 * @see java.math.BigDecimal
 *
 */
public final class BigDecimalUtils extends NumberUtils {

	/**
	 * <p>
	 * Formato padrão <code>#,##0.00</code> aplicado para valor decimal.
	 * </p>
	 */
	public static final String DEFAULT_DECIMAL_PATTERN = "#,##0.00";

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private BigDecimalUtils() {
		super();
	}

	/**
	 * <p>
	 * Faz um parse da <code>String</code> para produzir um <code>BigDecimal</code> de acordo com o formato informado. O
	 * parse é não lenient, ou seja, a entrada deve corresponder ao formato informado.
	 * </p>
	 *
	 * <p>
	 * <b>NOTA:</b> Caso o formato não seja informado será considerado o formato default
	 * {@value #DEFAULT_DECIMAL_PATTERN}.
	 * </p>
	 *
	 * <pre>
	 * BigDecimalUtils.parse("1.889000,99985")                  = 1889000.99985
	 * BigDecimalUtils.parse("1.889.000,99985")                 = 1889000.99985
	 * BigDecimalUtils.parse("1.889.000,999,85")                = 1889000.999
	 * BigDecimalUtils.parse(" 1.889000999,85 ")                = 1889000999.85
	 * BigDecimalUtils.parse("R$ 1889000999,85", "R$ #,##0.00") = 1889000999.85
	 * </pre>
	 *
	 * @param source
	 *        uma <code>String</code> que representa o valor decimal
	 * @param pattern
	 *        o formato decimal
	 *
	 * @return um <code>BigDecimal</code> criado a partir da <code>String</code>
	 *
	 * @throws ApplicationException
	 *         se a <code>String</code> informada não puder ser parseada
	 * @throws IllegalArgumentException
	 *         se a <code>String</code> ou o formato informado for <code>null</code>, vazio <code>("")</code> ou espaço
	 *         em branco <code>(" ")</code>
	 */
	public static BigDecimal parse(final String source, final String pattern) {
		try {
			if (StringUtils.isBlank(source)) {
				throw new IllegalArgumentException(BLANK_PARAMETER_MESSAGE);
			}

			DecimalFormat decimalFormat = new DecimalFormat(pattern);
			decimalFormat.setParseBigDecimal(true);

			BigDecimal bigDecimal = (BigDecimal) decimalFormat.parse(source.trim());

			return bigDecimal;

		} catch (ParseException e) {
			throw new ApplicationException(e);
		}
	}

	public static BigDecimal parse(final String source) {
		return parse(source, DEFAULT_DECIMAL_PATTERN);
	}

	/**
	 * <p>
	 * Faz um parse da <code>String</code> para produzir um valor numérico. Se o parse for lenient, então ao ocorrer uma
	 * exceção durante o parse será retornado <code>null</code>.
	 * </p>
	 *
	 * <p>
	 * <b>NOTA:</b> Caso o formato não seja informado será considerado o formato default
	 * {@value #DEFAULT_DECIMAL_PATTERN}.
	 * </p>
	 *
	 * <pre>
	 * BigDecimalUtils.parse("1.889000,99985", true)                  = 1889000.99985
	 * BigDecimalUtils.parse("1.889.000,99985", true)                 = 1889000.99985
	 * BigDecimalUtils.parse("1.889.000,999,85", true)                = 1889000.999
	 * BigDecimalUtils.parse(" 1.889000999,85 ", true)                = 1889000999.85
	 * BigDecimalUtils.parse("R$ 1889000999,85", true, "R$ #,##0.00") = 1889000999.85
	 * BigDecimalUtils.parse("", true)                                = null
	 * BigDecimalUtils.parse(" ", true)                               = null
	 * BigDecimalUtils.parse(null, true)                              = null
	 * </pre>
	 *
	 * @param source
	 *        uma <code>String</code> que representa o valor numérico
	 * @param lenient
	 *        um <code>boolean</code> que indica se o parse vai ser realizado de forma lenient ou não
	 * @param pattern
	 *        uma <code>String</code> que representa o formato utilizado no parseamento
	 *
	 * @return um <code>BigDecimal</code> criado a partir da <code>String</code>
	 *
	 * @exception IllegalArgumentException
	 *            se o texto informado for <code>null</code>, vazio <code>("")</code> ou espaço em branco
	 *            <code>(" ")</code> quando o lenient for <code>false</code>
	 */
	public static BigDecimal parse(final String source, final boolean lenient, final String pattern) {
		BigDecimal decimal = null;

		try {
			decimal = parse(source, pattern);

		} catch (Exception e) {
			if (!lenient) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}

		return decimal;
	}

	public static BigDecimal parse(final String source, final boolean lenient) {
		return parse(source, lenient, DEFAULT_DECIMAL_PATTERN);
	}

	/**
	 * <p>
	 * Retorna o <code>BigDecimal</code> informado ou <code>BigDecimal.ZERO</code> caso o valor informado seja
	 * <code>null</code>.
	 * </p>
	 *
	 * <pre>
	 * BigDecimalUtils.defaultBigDecimal(null)                            = 0
	 * BigDecimalUtils.defaultBigDecimal(new BigDecimal("188900099.985")) = 188900099.985
	 * </pre>
	 *
	 * @param decimal
	 *        valor a ser analisado
	 *
	 * @return o <code>BigDecimal</code> informado ou <code>BigDecimal.ZERO</code> se for <code>null</code>
	 *
	 */
	public static BigDecimal defaultBigDecimal(final BigDecimal decimal) {
		return decimal == null ? BigDecimal.ZERO : decimal;
	}

	public static BigDecimal defaultBigDecimal(final BigDecimal decimal, BigDecimal defaultValue) {
		return decimal == null ? defaultValue : decimal;
	}

	/**
	 * <p>
	 * Formata o decimal informado.
	 * </p>
	 *
	 * <p>
	 * <b>NOTA:</b> Caso o formato não seja informado será considerado o formato default
	 * {@value #DEFAULT_DECIMAL_PATTERN}.
	 * </p>
	 *
	 * <pre>
	 * BigDecimalUtils.format(new BigDecimal("1.98"))             = 1,98
	 * BigDecimalUtils.format(new BigDecimal("1188.98"))          = 1.188,98
	 * BigDecimalUtils.format(new BigDecimal("1188900099.98"))    = 1.188.900.099,98
	 * BigDecimalUtils.format(new BigDecimal("1188900099.98695")) = 1.188.900.099,99 // Arredondou para o próximo inteiro
	 * BigDecimalUtils.format(new BigDecimal("1188900099.98495")) = 1.188.900.099,98 // Arredondou para o inteiro anterior
	 * BigDecimalUtils.format(new BigDecimal("1188900099.9"))     = 1.188.900.099,90
	 * </pre>
	 *
	 * @param decimal
	 *        valor a ser analisado
	 * @param locale
	 *        locale a ser utilizado para a fotmatação
	 * @param pattern
	 *        formato a ser utilizado para mascarar o valor decimal
	 *
	 * @return valor formatado
	 *
	 */
	public static String format(final BigDecimal decimal, final Locale locale, final String pattern) {
		if (decimal == null) {
			return null;
		}

		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);

		return decimalFormat.format(decimal);
	}

	public static String format(final BigDecimal decimal, final String pattern) {
		return format(decimal, LocaleConstants.PORTUGUESE, pattern);
	}

	public static String format(final BigDecimal decimal) {
		return format(decimal, DEFAULT_DECIMAL_PATTERN);
	}

	public static boolean isZero(final BigDecimal decimal) {
		return isEqual(decimal, BigDecimal.ZERO);
	}

	public static boolean isZero(final BigDecimal decimal, final BigDecimal... decimals) {
		return isEqual(decimal, BigDecimal.ZERO, decimals);
	}

	public static boolean isEqual(final BigDecimal decimal1, final BigDecimal decimal2) {
		return decimal1.compareTo(decimal2) == 0;
	}

	public static boolean isEqual(final BigDecimal decimal1, final BigDecimal decimal2, final BigDecimal... decimals) {
		if (isNotEqual(decimal1, decimal2)) {
			return false;
		}

		for (BigDecimal decimal : decimals) {
			if (isNotEqual(decimal2, decimal)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isEqualAndGreaterThanZero(final BigDecimal decimal1, final BigDecimal decimal2) {
		return isEqual(decimal1, decimal2) && isGreaterThanZero(decimal1);
	}

	public static boolean isNotEqual(final BigDecimal decimal1, final BigDecimal decimal2) {
		return decimal1.compareTo(decimal2) != 0;
	}

	public static boolean isGreater(final BigDecimal decimal1, final BigDecimal decimal2) {
		return decimal1.compareTo(decimal2) > 0;
	}

	public static boolean isLess(final BigDecimal decimal1, final BigDecimal decimal2) {
		return decimal1.compareTo(decimal2) < 0;
	}

	public static boolean isGreaterThanZero(final BigDecimal decimal) {
		if (decimal == null) {
			return false;
		}

		return decimal.compareTo(BigDecimal.ZERO) > 0;
	}

}
