package br.com.afirmanet.core.util;

/**
 * Classe utilitária com métodos e constantes para formatação de intervalos de tempo.
 */
public final class DurationFormatUtils extends org.apache.commons.lang3.time.DurationFormatUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private DurationFormatUtils() {
		super();
	}

	public static String formatDurationWords(long durationMillis) {
		return org.apache.commons.lang3.time.DurationFormatUtils.formatDurationWords(durationMillis, true, true);
	}

}
