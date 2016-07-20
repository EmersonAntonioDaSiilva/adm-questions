package br.com.afirmanet.core.constant;

import java.nio.charset.Charset;

/**
 * <p>
 * Centraliza a definição de Charsets.
 * </p>
 */
public final class CharsetConstants {

	public static final Charset UTF8 = Charset.forName("UTF-8");

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private CharsetConstants() {
		// Classe utilitária não deve ter construtor público ou default
	}

}
