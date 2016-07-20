package br.com.afirmanet.core.util;

import java.math.BigDecimal;

/**
 * <p>
 * Classe utilitária para manipulação de <code>Boolean</code>.
 * </p>
 */
public final class BooleanUtils {

	public static final String SIM = "S";
	public static final String NAO = "N";

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private BooleanUtils() {
		super();
	}

	public static Boolean parse(final String source) {
		if (source == null) {
			return false;
		}

		return Boolean.valueOf(source.toUpperCase().equals(SIM) ? true : false);
	}

	public static Boolean parse(BigDecimal source) {
		if (source == null) {
			return false;
		}

		return source.intValue() == 1;
	}

}
