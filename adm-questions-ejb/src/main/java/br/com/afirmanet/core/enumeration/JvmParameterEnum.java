package br.com.afirmanet.core.enumeration;

/**
 * <p>
 * Enumera os parâmetros que podem ser definidos na JVM do servidor de aplicação.
 * </p>
 */
public enum JvmParameterEnum {

	/**
	 * Nome do servidor, seguindo o padrão: 192.168.10.78-PGE-DA-01.
	 */
	SERVER_NAME("nomeServidor");

	private String value;

	private JvmParameterEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}