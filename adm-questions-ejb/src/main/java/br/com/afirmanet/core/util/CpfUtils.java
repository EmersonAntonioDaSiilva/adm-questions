package br.com.afirmanet.core.util;

import java.util.regex.Pattern;

import br.com.afirmanet.core.document.Document;

/**
 * <p>
 * Classe utilitária para manipulação de CPF (Cadastro de Pessoa Física).
 * </p>
 * 
 * <ul>
 * <li><b>format</b> - formata o CPF, deixando-o com a seguinte máscara: 999.999.999-99</li>
 * <li><b>isFormatted</b> - verifica se o CPF está formatado</li>
 * <li><b>isValid</b> - verifica se o CPF é válido</li>
 * <li><b>getCheckDigits</b> - recupera os dígitos verificadores calculados</li>
 * <li><b>isLegible</b> - verifica se o CPF é legível</li>
 * <li><b>leftPad</b> - completa com zeros a esquerda</li>
 * </ul>
 * 
 * @see CnpjUtils
 * @see CpfCnpjUtils
 * 
 */
public final class CpfUtils extends Document {

	private static final Pattern FORMATTED_REGEX = Pattern.compile("(\\d{3})[.](\\d{3})[.](\\d{3})-(\\d{2})");

	private static final Pattern UNFORMATTED_REGEX = Pattern.compile("(\\d{3})(\\d{3})(\\d{3})(\\d{2})");

	private static final int UNFORMATTED_SIZE = 11;

	private static final String MASK = "###.###.###-##";

	private static final int[] CHECK_DIGIT_1_MULTIPLIERS = { 10, 9, 8, 7, 6, 5, 4, 3, 2 };

	private static final int[] CHECK_DIGIT_2_MULTIPLIERS = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };

	private static final String MESSAGE_ERROR = "CPF informado não é legível, ou seja, não está em um formato válido. Ex: 000.000.001-91 ou 00000000191";

	private static final String INVALID_CPF = "00000000000";

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private CpfUtils() {
		super();
	}

	/**
	 * <p>
	 * Formata o CPF informado.
	 * </p>
	 * 
	 * <pre>
	 * CpfUtils.format("00000000191")    = 000.000.001-91
	 * CpfUtils.format("000.000.001-91") = 000.000.001-91
	 * </pre>
	 * 
	 * @param cpf
	 *        CPF a ser formatado
	 * 
	 * @return CPF formatado
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cpf</code> estiver <code>null</code>, vazio <code>("")</code>, em branco
	 *         <code>(" ")</code> ou não estiver legível
	 * 
	 */
	public static String format(final String cpf) {
		if (cpf == null) {
			return null;
		}

		if (!isLegible(cpf)) {
			throw new IllegalArgumentException(MESSAGE_ERROR);
		}

		String formattedCpf = StringUtils.applyMask(cpf, MASK);

		return formattedCpf;
	}

	/**
	 * <p>
	 * Verifica se o CPF informado está formatado.
	 * </p>
	 * 
	 * <pre>
	 * CpfUtils.isFormatted("000.000.001-91") = true
	 * CpfUtils.isFormatted("00000000191")    = false
	 * </pre>
	 * 
	 * @param cpf
	 *        CPF a ser avaliado
	 * 
	 * @return <code>true</code> se o CPF estiver formatado, caso contrário <code>false</code>
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cpf</code> estiver <code>null</code>, vazio <code>("")</code>, em branco
	 *         <code>(" ")</code> ou não estiver legível
	 * 
	 */
	public static boolean isFormatted(final String cpf) {
		if (!isLegible(cpf)) {
			throw new IllegalArgumentException(MESSAGE_ERROR);
		}

		boolean isFormatted = FORMATTED_REGEX.matcher(cpf).matches();

		return isFormatted;
	}

	/**
	 * <p>
	 * Verifica se o CPF informado é válido.
	 * </p>
	 * 
	 * <pre>
	 * CpfUtils.isValid("000.000.001-91") = true
	 * CpfUtils.isValid("00000000191")    = true
	 * CpfUtils.isValid("954.347.565-10") = false // Dígito correto é 25
	 * CpfUtils.isValid("00000000000")    = false
	 * CpfUtils.isValid("123456")         = false
	 * CpfUtils.isValid("abc")            = false
	 * </pre>
	 * 
	 * <p>
	 * <b>NOTA:</b> Caso o CPF informado seja <code>null</code>, vazio <code>("")</code> ou espaço branco
	 * <code>(" ")</code>, será retornado <code>false</code>.
	 * <p>
	 * 
	 * @param cpf
	 *        CPF a ser avaliado
	 * 
	 * @return <code>true</code> se o CPF for válido, caso contrário <code>false</code>
	 * 
	 */
	public static boolean isValid(final String cpf) {
		String cpfClean = StringUtils.removeNonDigits(cpf);
		if (INVALID_CPF.equals(cpfClean)) {
			return false;
		}

		boolean isValid;
		try {
			String checkDigits = getDigitsCheckers(cpf);
			String checkDigitsInEvaluation = cpf.substring(cpf.length() - 2, cpf.length());
			isValid = checkDigits.equals(checkDigitsInEvaluation);

		} catch (IllegalArgumentException e) {
			isValid = false;
		}

		return isValid;
	}

	/**
	 * <p>
	 * Obtém os dígitos verificadores (posição 10 e 11) do CPF informado, isto é, calcula os dígitos verificadores com
	 * base nas posições 1 a 9.
	 * </p>
	 * 
	 * <pre>
	 * CpfUtils.getCheckDigits("000.000.001-91") = 91 
	 * CpfUtils.getCheckDigits("00000000191")    = 91
	 * CpfUtils.getCheckDigits("954.347.565-10") = 25 // Dígitos verificadores corretos
	 * CpfUtils.getCheckDigits("95434756510")    = 25 // Dígitos verificadores corretos
	 * </pre>
	 * 
	 * @param cpf
	 *        CPF a ser considerado para cálculo dos dígitos verificadores
	 * 
	 * @return dígitos verificadores calculados para o CPF informado
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cpf</code> estiver <code>null</code>, vazio <code>("")</code> ou espaço branco
	 *         <code>(" ")</code>
	 * 
	 */
	public static String getDigitsCheckers(final String cpf) {
		if (!isLegible(cpf)) {
			throw new IllegalArgumentException(MESSAGE_ERROR);
		}

		return getCpfCnpjCheckDigits(cpf, CHECK_DIGIT_1_MULTIPLIERS, CHECK_DIGIT_2_MULTIPLIERS);
	}

	/**
	 * <p>
	 * Verifica se o CPF informado é legível, ou seja, possui um formato passível de leitura.
	 * </p>
	 * 
	 * <pre>
	 * CpfUtils.isLegible("000.000.001-91") = true
	 * CpfUtils.isLegible("00000000191")    = true
	 * CpfUtils.isLegible("00000000000")    = true
	 * CpfUtils.isLegible("123456")         = false
	 * CpfUtils.isLegible("abc")            = false
	 * </pre>
	 * 
	 * @param cpf
	 *        CPF a ser avaliado
	 * 
	 * @return <code>true</code> se o CPF for legível, caso contrário <code>false</code>
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cpf</code> estiver <code>null</code>, vazio <code>("")</code> ou espaço branco
	 *         <code>(" ")</code>
	 * 
	 */
	public static boolean isLegible(final String cpf) {
		return isLegible(cpf, FORMATTED_REGEX, UNFORMATTED_REGEX);
	}

	/**
	 * <p>
	 * Completa com zeros as esquerda até atingir a quantidade máxima de dígitos considerado para um CPF.
	 * </p>
	 * 
	 * <pre>
	 * CpfUtils.leftPad("65464861564") 	= 65464861564
	 * CpfUtils.leftPad("191")     		= 00000000191
	 * CpfUtils.leftPad("0")            = 00000000000
	 * CpfUtils.leftPad("")             = 00000000000
	 * </pre>
	 * 
	 * @param cpf
	 *        CPF a ser avaliado
	 * 
	 * @return CPF com zeros acrescentados a esquerda
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cpf</code> estiver <code>null</code>, possuir espaços em branco <code>(" ")</code>
	 *         ou caracteres/símbolos
	 * 
	 */
	public static String leftPad(final String cpf) {
		if (!StringUtils.isNumeric(cpf)) {
			throw new IllegalArgumentException("Parâmetro de entrada deve ser composto apenas de caracteres numéricos.");
		}

		if (cpf.length() > UNFORMATTED_SIZE) {
			throw new IllegalArgumentException("Parâmetro informado não corresponde a um CPF. CPF possui " + UNFORMATTED_SIZE + " dígitos.");
		}

		return StringUtils.leftPad(cpf, UNFORMATTED_SIZE, '0');
	}

}
