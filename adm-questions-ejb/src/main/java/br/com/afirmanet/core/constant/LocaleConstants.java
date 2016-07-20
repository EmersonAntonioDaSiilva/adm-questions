package br.com.afirmanet.core.constant;

import java.util.Locale;

/**
 * <p>
 * Centraliza a definição de locales, não sendo necessário saber as abreviações de 'language' e 'country' no momento da
 * utilização de locales.
 * </p>
 */
public final class LocaleConstants {

	public static final Locale PORTUGUESE = new Locale("pt", "BR");

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private LocaleConstants() {
		// Classe utilitária não deve ter construtor público ou default
	}
}
