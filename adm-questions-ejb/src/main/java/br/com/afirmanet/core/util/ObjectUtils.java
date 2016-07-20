package br.com.afirmanet.core.util;

/**
 * <p>
 * Classe utilitária para manipulação de Objetos.
 * </p>
 */
public final class ObjectUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private ObjectUtils() {
		super();
	}

	public static boolean isNull(Object... objects) {
		for (Object object : objects) {
			if (object != null) {
				return false;
			}
		}

		return true;
	}

	public static boolean anyIsNotNull(Object... object) {
		return !isNull(object);
	}

}
