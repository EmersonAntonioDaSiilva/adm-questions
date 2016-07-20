package br.com.afirmanet.core.document.ie;

import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.text.MaskFormatter;

import org.apache.commons.lang3.StringUtils;

import br.com.afirmanet.core.document.exception.IeException;

public abstract class AbstractIe {

	static Pattern formattedRegex;
	static Pattern unformattedRegex;
	static String mask;

	public boolean isLegible(final String document) {
		if (StringUtils.isEmpty(document)) {
			throw new IeException("Parâmetro não pode ser vazio!");
		}

		return formattedRegex.matcher(document).matches() || unformattedRegex.matcher(document).matches();
	}

	public String format(final String document) {
		try {
			if (document == null) {
				return null;
			}

			if (!isLegible(document)) {
				throw new IeException("Documento informado não é legível, ou seja, não está em um formato válido!");
			}

			MaskFormatter maskFormatter = new MaskFormatter(mask);
			maskFormatter.setValueContainsLiteralCharacters(false);
			return maskFormatter.valueToString(document);

		} catch (ParseException e) {
			throw new IeException(e);
		}
	}

	public boolean isFormatted(final String document) {
		if (!isLegible(document)) {
			throw new IeException("Documento informado não é legível, ou seja, não está em um formato válido!");
		}

		return formattedRegex.matcher(document).matches();
	}

	public boolean validate(final String document) {
		try {
			if (!isLegible(document)) {
				return false;
			}

			String checkDigits = getCheckDigits(document);
			String checkDigitsInEvaluation = document.substring(document.length() - checkDigits.length(), document.length());
			return checkDigits.equals(checkDigitsInEvaluation);

		} catch (IeException e) {
			return false;
		}
	}

	/**
	 * <p>
	 * Obtém os dígitos verificadores da Inscrição Estadual informada a partir do cálculo apropriado para o tipo de
	 * Inscrição.
	 * </p>
	 *
	 * <pre>
	 * Ie inscricaoEstadual = IeFactory.getInscricaoEstadual(UfEnum.AC);
	 * String checkDigits = inscricaoEstadual.getCheckDigits(&quot;01.004.823/001-12&quot;);
	 *
	 * System.out.println(&quot;Digitos Verificadores gerados são: &quot; + checkDigits); // Saída será 12
	 * </pre>
	 *
	 * @param document
	 *        Ie que se deseja obter os dígitos verificadores calculados
	 *
	 * @return dígitos verificadores calculados
	 *
	 * @throws IeException
	 *         se o parâmetro não tiver um formato de IE válido
	 *
	 */
	public abstract String getCheckDigits(final String document);

}
