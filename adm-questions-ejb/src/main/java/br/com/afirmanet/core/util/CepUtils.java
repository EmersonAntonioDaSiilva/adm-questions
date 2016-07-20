package br.com.afirmanet.core.util;

import java.util.regex.Pattern;

import br.com.afirmanet.core.document.Document;

public class CepUtils extends Document {

	private static final Pattern FORMATTED_REGEX = Pattern.compile("(\\d{5})-(\\d{3})");

	private static final Pattern UNFORMATTED_REGEX = Pattern.compile("(\\d{8})");

	private static final String MASK = "#####-###";

	private static final String MESSAGE_ERROR = "CEP informado não está em um formato válido. Ex: 00000-000";

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private CepUtils() {
		super();
	}

	public static String format(final String cep) {
		if (cep == null) {
			return null;
		}

		if (cep.length() != 8) {
			return cep;
		}

		if (!isLegible(cep)) {
			throw new IllegalArgumentException(MESSAGE_ERROR);
		}

		String formattedCep = StringUtils.applyMask(cep, MASK);

		return formattedCep;
	}

	public static boolean isFormatted(final String cep) {
		if (!isLegible(cep)) {
			throw new IllegalArgumentException(MESSAGE_ERROR);
		}

		boolean isFormatted = FORMATTED_REGEX.matcher(cep).matches();

		return isFormatted;
	}

	public static boolean isLegible(final String cpf) {
		return isLegible(cpf, FORMATTED_REGEX, UNFORMATTED_REGEX);
	}

}