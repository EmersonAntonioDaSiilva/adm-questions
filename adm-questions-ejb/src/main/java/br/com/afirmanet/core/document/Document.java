package br.com.afirmanet.core.document;

import java.util.regex.Pattern;

import br.com.afirmanet.core.util.StringUtils;

/**
 * <p>
 * Classe utilitária básica para manipulação de documentos.
 * </p>
 */
public class Document {

	private static final int MODULE = 11;

	private static final String LEGIBILITY_ERROR_MESSAGE = "Documento informado não é legível, ou seja, não está em um formato válido.";

	/**
	 * <p>
	 * Verifica se o documento informado é legível, ou seja, possui um formato passível de leitura.
	 * </p>
	 *
	 * <pre>
	 * Pattern formattedRegex = Pattern.compile("(\\d{3})[.](\\d{3})[.](\\d{3})[.](\\d{3})"); // Pattern para IE São Paulo
	 * Pattern unformattedRegex = Pattern.compile("(\\d{3})(\\d{3})(\\d{3})(\\d{3})"); // Pattern para IE São Paulo
	 *
	 * Document.isLegible("123.456.789.012", formattedRegex, unformattedRegex) = true
	 * Document.isLegible("123456789012", formattedRegex, unformattedRegex)    = true
	 * Document.isLegible("000000000000", formattedRegex, unformattedRegex)    = true
	 * Document.isLegible("123456", formattedRegex, unformattedRegex)          = false
	 * Document.isLegible("abc", formattedRegex, unformattedRegex)             = false
	 * </pre>
	 *
	 * @param document
	 *        documento a ser avaliado
	 * @param formattedRegex
	 *        um <code>Pattern</code> que representa um documento formatado
	 * @param unformattedRegex
	 *        um <code>Pattern</code> que representa um documento não formatado
	 *
	 * @return <code>true</code> se o documento for legível, caso contrário <code>false</code>
	 *
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>document</code> estiver <code>null</code>, vazio <code>("")</code> ou com espaço em
	 *         branco <code>(" ")</code>
	 *
	 */
	protected static boolean isLegible(final String document, final Pattern formattedRegex, final Pattern unformattedRegex) {
		if (StringUtils.isEmpty(document)) {
			throw new IllegalArgumentException("Parâmetro não pode ser vazio.");
		}

		boolean isLegible = formattedRegex.matcher(document).matches() || unformattedRegex.matcher(document).matches();

		return isLegible;
	}

	/**
	 * <p>
	 * Formata o documento informado.
	 * </p>
	 *
	 * <pre>
	 * Document.format("123456789012", "###.###.###.###", true)    = 123.456.789.012
	 * Document.format("123.456.789.012", "###.###.###.###", true) = 123.456.789.012
	 * </pre>
	 *
	 * @param document
	 *        documento a ser formatado
	 * @param mask
	 *        uma <code>String</code> que representa a máscara utilizada na formatação
	 * @param isLegible
	 *        um <code>boolean</code> que indica se o documento é legível ou não
	 *
	 * @return o documento formatado
	 *
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>document</code> estiver vazio <code>("")</code>, em branco <code>(" ")</code> ou não
	 *         estiver legível
	 *
	 */
	protected static String format(final String document, final String mask, final boolean isLegible) {
		if (!isLegible) {
			throw new IllegalArgumentException(LEGIBILITY_ERROR_MESSAGE);
		}

		String formattedDocument = StringUtils.applyMask(document, mask);

		return formattedDocument;
	}

	protected static String getCpfCnpjCheckDigits(final String cnpj, int[] checkDigit1Multipliers, int[] checkDigit2Multipliers) {
		String cnpjClean = StringUtils.removeNonDigits(cnpj);
		String cnpjWithoutCheckDigits = cnpjClean.substring(0, cnpjClean.length() - 2);
		int checkDigit1 = 0;
		int checkDigit2 = 0;

		for (int position = 1; position <= cnpjWithoutCheckDigits.length(); position++) {
			int digit = Integer.parseInt(cnpjWithoutCheckDigits.substring(position - 1, position));
			checkDigit1 += checkDigit1Multipliers[position - 1] * digit;
			checkDigit2 += checkDigit2Multipliers[position - 1] * digit;
		}

		// Cálculo para obtenção do dígito verificador 1
		int rest = checkDigit1 % MODULE;
		if (rest < 2) {
			checkDigit1 = 0;
		} else {
			checkDigit1 = MODULE - rest;
		}

		// Cálculo para obtenção do dígito verificador 2
		checkDigit2 += checkDigit2Multipliers[checkDigit2Multipliers.length - 1] * checkDigit1;
		rest = checkDigit2 % MODULE;
		if (rest < 2) {
			checkDigit2 = 0;
		} else {
			checkDigit2 = MODULE - rest;
		}

		return String.valueOf(checkDigit1) + checkDigit2;
	}

}
