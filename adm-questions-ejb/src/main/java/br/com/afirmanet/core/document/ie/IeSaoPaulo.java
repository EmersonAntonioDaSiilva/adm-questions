package br.com.afirmanet.core.document.ie;

import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.text.MaskFormatter;

import br.com.afirmanet.core.document.exception.IeException;
import br.com.afirmanet.core.util.StringUtils;

/**
 * <p>
 * Documentação de referência:
 * </p>
 * <a href="http://www.pfe.fazenda.sp.gov.br/consist_ie.shtm">Secretaria da Fazenda do Estado de São Paulo</a> e <a
 * href="http://www.sintegra.gov.br/Cad_Estados/cad_SP.html">SINTEGRA - ROTEIRO DE CRÍTICA DA INSCRIÇÃO ESTADUAL </a>
 *
 */
public class IeSaoPaulo extends AbstractIe implements Ie {

	private static final int[] CHECK_DIGIT_1_MULTIPLIERS = { 1, 3, 4, 5, 6, 7, 8, 10 };
	private static final int[] CHECK_DIGIT_2_MULTIPLIERS = { 3, 2, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
	private static final int MODULE = 11;
	private static final String MASK_PRODUTOR_RURAL = "#-########.#/###";

	static {
		formattedRegex = Pattern.compile("((\\d{3})[.](\\d{3})[.](\\d{3})[.](\\d{3}))|(P-(\\d{8})[.](\\d{1})[/](\\d{3}))");
		unformattedRegex = Pattern.compile("((\\d{3})(\\d{3})(\\d{3})(\\d{3}))|(P(\\d{8})(\\d{1})(\\d{3}))");
		mask = "###.###.###.###";
	}

	@Override
	public String format(final String document) {
		try {
			if (document == null) {
				return null;
			}

			if (!isLegible(document)) {
				throw new IeException("Documento informado não é legível, ou seja, não está em um formato válido!");
			}

			String newMask = document.length() == 12 ? mask : MASK_PRODUTOR_RURAL;

			MaskFormatter maskFormatter = new MaskFormatter(newMask);
			maskFormatter.setValueContainsLiteralCharacters(false);
			return maskFormatter.valueToString(document);

		} catch (ParseException e) {
			throw new IeException(e);
		}
	}

	@Override
	public String getCheckDigits(final String document) {
		if (!isLegible(document)) {
			throw new IeException("Documento informado não é legível, ou seja, não está em um formato válido!");
		}

		String documentClean = StringUtils.removeNonDigits(document);
		int checkDigit1 = 0;
		int checkDigit2 = 0;
		String checksDigits = null;

		// INDUSTRIAS E COMERCIANTES
		if (documentClean.length() == 12) {
			// Cálculo para obtenção do dígito verificador 1
			String documentNonCheckDigits = documentClean.substring(0, documentClean.length() - 4);

			for (int position = 1; position <= documentNonCheckDigits.length(); position++) {
				int digit = Integer.parseInt(documentNonCheckDigits.substring(position - 1, position));
				checkDigit1 += CHECK_DIGIT_1_MULTIPLIERS[position - 1] * digit;
			}

			int rest = checkDigit1 % MODULE;
			String strRest = String.valueOf(rest);
			checkDigit1 = Integer.parseInt(strRest.substring(strRest.length() - 1));

			// Cálculo para obtenção do dígito verificar 2
			documentNonCheckDigits = documentClean.substring(0, documentClean.length() - 1);

			for (int position = 1; position <= documentNonCheckDigits.length(); position++) {
				int digit = Integer.parseInt(documentNonCheckDigits.substring(position - 1, position));
				checkDigit2 += CHECK_DIGIT_2_MULTIPLIERS[position - 1] * digit;
			}

			rest = checkDigit2 % MODULE;
			strRest = String.valueOf(rest);
			checkDigit2 = Integer.parseInt(strRest.substring(strRest.length() - 1));

			checksDigits = String.valueOf(checkDigit1) + checkDigit2;

		} else { // PRODUTOR RURAL
			// Cálculo para obtenção do dígito verificador 1
			String documentNonCheckDigits = documentClean.substring(0, documentClean.length() - 4);
			documentNonCheckDigits = documentNonCheckDigits.substring(1); // desconsiderar a letra P

			for (int position = 1; position <= documentNonCheckDigits.length(); position++) {
				int digit = Integer.parseInt(documentNonCheckDigits.substring(position - 1, position));
				checkDigit1 += CHECK_DIGIT_1_MULTIPLIERS[position - 1] * digit;
			}

			int rest = checkDigit1 % MODULE;
			String strRest = String.valueOf(rest);
			checkDigit1 = Integer.parseInt(strRest.substring(strRest.length() - 1));

			checksDigits = String.valueOf(checkDigit1);
		}

		return checksDigits;
	}

	@Override
	public boolean validate(final String document) {
		try {
			if (!isLegible(document)) {
				return false;
			}

			String documentClean = StringUtils.removeNonDigits(document);

			String checkDigits = getCheckDigits(document);
			String checkDigitsInEvaluation = String.format("%s%s", documentClean.charAt(CHECK_DIGIT_1_MULTIPLIERS.length), documentClean.charAt(CHECK_DIGIT_2_MULTIPLIERS.length));
			return checkDigits.equals(checkDigitsInEvaluation);

		} catch (IeException e) {
			return false;
		}
	}

}
