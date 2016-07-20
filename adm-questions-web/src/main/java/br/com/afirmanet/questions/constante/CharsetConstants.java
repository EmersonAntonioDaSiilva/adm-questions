package br.com.afirmanet.questions.constante;

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
