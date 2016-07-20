package br.com.afirmanet.core.util;

import java.util.regex.Pattern;

import br.com.afirmanet.core.document.Document;

/**
 * <p>
 * Classe utilitária para manipulação de CNPJ (Cadastro Nacional de Pessoa Jurídica).
 * </p>
 * 
 * <ul>
 * <li><b>format</b> - formata o CNPJ, deixando-o com a seguinte máscara: 99.999.999/9999-99</li>
 * <li><b>isFormatted</b> - verifica se o CNPJ está formatado</li>
 * <li><b>isValid</b> - verifica se o CNPJ é válido</li>
 * <li><b>getCheckDigits</b> - recupera os dígitos verificadores calculados</li>
 * <li><b>isLegible</b> - verifica se o CNPJ é legível</li>
 * <li><b>leftPad</b> - completa com zeros a esquerda</li>
 * </ul>
 * 
 * @see CpfUtils
 * @see CpfCnpjUtils
 * 
 */
public final class CnpjUtils extends Document {

	private static final Pattern FORMATTED_REGEX = Pattern.compile("(\\d{2})[.](\\d{3})[.](\\d{3})/(\\d{4})-(\\d{2})");
	private static final Pattern FORMATTED_REGEX_CNPJ_BASE = Pattern.compile("(\\d{2})[.](\\d{3})[.](\\d{3})");

	private static final Pattern UNFORMATTED_REGEX = Pattern.compile("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})");
	private static final Pattern UNFORMATTED_REGEX_CNPJ_BASE = Pattern.compile("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})");

	private static final int UNFORMATTED_SIZE = 14;

	private static final String MASK = "##.###.###/####-##";
	private static final String MASK_CNPJ_BASE = "##.###.###";

	private static final int[] CHECK_DIGIT_1_MULTIPLIERS = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

	private static final int[] CHECK_DIGIT_2_MULTIPLIERS = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

	private static final String MESSAGE_ERROR = "CNPJ informado não é legível, ou seja, não está em um formato válido. Ex: 00.000.000/0001-91 ou 000000000000191";
	private static final String MESSAGE_ERROR_CNPJ_BASE = "CNPJ informado não é legível, ou seja, não está em um formato válido. Ex: 00.000.000 ou 00000000";

	private static final String INVALID_CNPJ = "00000000000000";

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private CnpjUtils() {
		super();
	}

	/**
	 * <p>
	 * Formata o CNPJ informado.
	 * </p>
	 * 
	 * <pre>
	 * CnpjUtils.format("00000000000191")     = 00.000.000/0001-91
	 * CnpjUtils.format("00.000.000/0001-91") = 00.000.000/0001-91
	 * </pre>
	 * 
	 * @param cnpj
	 *        CNPJ a ser formatado
	 * 
	 * @return CNPJ formatado
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cnpj</code> estiver vazio <code>("")</code>, em branco <code>(" ")</code> ou não
	 *         estiver legível
	 * 
	 */
	public static String format(final String cnpj) {
		if (cnpj == null) {
			return null;
		}

		if (!isLegible(cnpj)) {
			throw new IllegalArgumentException(MESSAGE_ERROR);
		}

		String formattedCnpj = StringUtils.applyMask(cnpj, MASK);

		return formattedCnpj;
	}

	public static String formatCNPJBase(final String cnpjBase) {
		if (cnpjBase == null) {
			return null;
		}

		if (!isLegibleCNPJBase(cnpjBase)) {
			throw new IllegalArgumentException(MESSAGE_ERROR_CNPJ_BASE);
		}

		String formattedCnpj = StringUtils.applyMask(cnpjBase, MASK_CNPJ_BASE);

		return formattedCnpj;
	}

	/**
	 * <p>
	 * Verifica se o CNPJ informado está formatado.
	 * </p>
	 * 
	 * <pre>
	 * CnpjUtils.isFormatted("00.000.000/0001-91") = true
	 * CnpjUtils.isFormatted("00000000000191")     = false
	 * </pre>
	 * 
	 * @param cnpj
	 *        CNPJ a ser avaliado
	 * 
	 * @return <code>true</code> se o CNPJ estiver formatado, caso contrário <code>false</code>
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cnpj</code> estiver <code>null</code>, vazio <code>("")</code>, em branco
	 *         <code>(" ")</code> ou não estiver legível
	 * 
	 */
	public static boolean isFormatted(final String cnpj) {
		if (!isLegible(cnpj)) {
			throw new IllegalArgumentException(MESSAGE_ERROR);
		}

		boolean isFormatted = FORMATTED_REGEX.matcher(cnpj).matches();

		return isFormatted;
	}

	/**
	 * <p>
	 * Verifica se o CNPJ informado é válido.
	 * </p>
	 * 
	 * <pre>
	 * CnpjUtils.isValid("00.000.000/0001-91") = true
	 * CnpjUtils.isValid("00000000000191")     = true
	 * CnpjUtils.isValid("40.695.212/0001-12") = false // Dígito correto é 55
	 * CnpjUtils.isValid("00000000000000")     = false
	 * CnpjUtils.isValid("123456")             = false
	 * CnpjUtils.isValid("abc")                = false
	 * </pre>
	 * 
	 * <p>
	 * <b>NOTA:</b> Caso o CNPJ informado seja <code>null</code>, vazio <code>("")</code> ou espaço branco
	 * <code>(" ")</code>, será retornado <code>false</code>.
	 * <p>
	 * 
	 * @param cnpj
	 *        CNPJ a ser avaliado
	 * 
	 * @return <code>true</code> se o CNPJ for válido, caso contrário <code>false</code>
	 * 
	 */
	public static boolean isValid(final String cnpj) {
		String cnpjClean = StringUtils.removeNonDigits(cnpj);
		if (INVALID_CNPJ.equals(cnpjClean)) {
			return false;
		}

		boolean isValid;
		try {
			String checkDigits = getCheckDigits(cnpj);
			String checkDigitsInEvaluation = cnpj.substring(cnpj.length() - 2, cnpj.length());
			isValid = checkDigits.equals(checkDigitsInEvaluation);

		} catch (IllegalArgumentException e) {
			isValid = false;
		}

		return isValid;
	}

	/**
	 * <p>
	 * Obtém os dígitos verificadores (posição 13 e 14) do CNPJ informado, isto é, calcula os dígitos verificadores com
	 * base nas posições 1 a 12.
	 * </p>
	 * 
	 * <pre>
	 * CnpjUtils.getCheckDigits("00.000.000/0001-91") = 91 
	 * CnpjUtils.getCheckDigits("00000000000191")     = 91
	 * CnpjUtils.getCheckDigits("40.695.212/0001-12") = 55 // Dígitos verificadores corretos
	 * CnpjUtils.getCheckDigits("40695212000112")     = 55 // Dígitos verificadores corretos
	 * </pre>
	 * 
	 * @param cnpj
	 *        CNPJ a ser considerado para cálculo dos dígitos verificadores
	 * 
	 * @return dígitos verificadores calculados para o CNPJ informado
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cnpj</code> estiver <code>null</code>, vazio <code>("")</code> ou espaço branco
	 *         <code>(" ")</code>
	 * 
	 */
	public static String getCheckDigits(final String cnpj) {
		if (!isLegible(cnpj)) {
			throw new IllegalArgumentException(MESSAGE_ERROR);
		}

		return getCpfCnpjCheckDigits(cnpj, CHECK_DIGIT_1_MULTIPLIERS, CHECK_DIGIT_2_MULTIPLIERS);
	}

	/**
	 * <p>
	 * Verifica se o CNPJ informado é legível, ou seja, possui um formato passível de leitura.
	 * </p>
	 * 
	 * <pre>
	 * CnpjUtils.isLegible("00.000.000/0001-91") = true
	 * CnpjUtils.isLegible("00000000000191")     = true
	 * CnpjUtils.isLegible("00000000000000")     = true
	 * CnpjUtils.isLegible("123456")             = false
	 * CnpjUtils.isLegible("abc")                = false
	 * </pre>
	 * 
	 * @param cnpj
	 *        CNPJ a ser avaliado
	 * 
	 * @return <code>true</code> se o CNPJ for legível, caso contrário <code>false</code>
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cnpj</code> estiver <code>null</code>, vazio <code>("")</code> ou espaço branco
	 *         <code>(" ")</code>
	 * 
	 */
	public static boolean isLegible(final String cnpj) {
		return isLegible(cnpj, FORMATTED_REGEX, UNFORMATTED_REGEX);
	}

	public static boolean isLegibleCNPJBase(final String cnpjBase) {
		return isLegible(cnpjBase, FORMATTED_REGEX_CNPJ_BASE, UNFORMATTED_REGEX_CNPJ_BASE);
	}

	/**
	 * <p>
	 * Completa com zeros as esquerda até atingir a quantidade máxima de dígitos considerado para um CNPJ.
	 * </p>
	 * 
	 * <pre>
	 * CnpjUtils.leftPad("46170141000153") 	= 46170141000153
	 * CnpjUtils.leftPad("191")     		= 00000000000191
	 * CnpjUtils.leftPad("0") 				= 00000000000000
	 * CnpjUtils.leftPad("") 				= 00000000000000
	 * </pre>
	 * 
	 * @param cnpj
	 *        CNPJ a ser avaliado
	 * 
	 * @return CNPJ com zeros acrescentados a esquerda
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cnpj</code> estiver <code>null</code>, possuir espaços em branco <code>(" ")</code>
	 *         ou caracteres/símbolos
	 * 
	 */
	public static String leftPad(final String cnpj) {
		if (!StringUtils.isNumeric(cnpj)) {
			throw new IllegalArgumentException("Parâmetro de entrada deve ser composto apenas de caracteres numéricos.");
		}

		if (cnpj.length() > UNFORMATTED_SIZE) {
			throw new IllegalArgumentException("Parâmetro informado não corresponde a um CNPJ. CNPJ possui " + UNFORMATTED_SIZE + " dígitos.");
		}

		return StringUtils.leftPad(cnpj, UNFORMATTED_SIZE, '0');
	}

}
