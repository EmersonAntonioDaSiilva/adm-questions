package br.com.afirmanet.core.util;

public final class ProxyUtils {

	/**
	 * <p>
	 * Chave que representa o nome da variável responsável por armazenar o host do proxy utilizado na rede.
	 * </p>
	 */
	private static final String SERVIDOR_PROXY_HTTP = "http.proxyHost";

	/**
	 * <p>
	 * Chave que representa o nome da variável responsável por armazenar a porta do proxy utilizada na rede.
	 * </p>
	 */
	private static final String PORTA_PROXY_HTTP = "http.proxyPort";

	/**
	 * <p>
	 * Chave que representa o nome da variável responsável por armazenar o host (SSL) do proxy utilizado na rede.
	 * </p>
	 */
	private static final String SERVIDOR_PROXY_HTTPS = "https.proxyHost";

	/**
	 * <p>
	 * Chave que representa o nome da variável responsável por armazenar a porta (SSL) do proxy utilizada na rede.
	 * </p>
	 */
	private static final String PORTA_PROXY_HTTPS = "https.proxyPort";

	private ProxyUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	/**
	 * <p>
	 * Habilita o proxy utilizado na rede, facilitando o manuseio de webservices interno (mesma rede) e externo
	 * (internet).
	 * </p>
	 * 
	 * @param hostProxyHttp
	 *        servidor proxy HTTP
	 * @param portProxyHttp
	 *        porta proxy HTTP
	 * @param hostProxyHttps
	 *        servidor proxy HTTPS
	 * @param portProxyHttps
	 *        porta proxy HTTPS
	 */
	public static void enable(String hostProxyHttp, String portProxyHttp, String hostProxyHttps, String portProxyHttps) {
		if (StringUtils.isBlank(System.getProperty(SERVIDOR_PROXY_HTTP))) {
			System.setProperty(SERVIDOR_PROXY_HTTP, hostProxyHttp);
		}

		if (StringUtils.isBlank(System.getProperty(PORTA_PROXY_HTTP))) {
			System.setProperty(PORTA_PROXY_HTTP, portProxyHttp);
		}

		if (StringUtils.isBlank(System.getProperty(SERVIDOR_PROXY_HTTPS))) {
			System.setProperty(SERVIDOR_PROXY_HTTPS, hostProxyHttps);
		}

		if (StringUtils.isBlank(System.getProperty(PORTA_PROXY_HTTPS))) {
			System.setProperty(PORTA_PROXY_HTTPS, portProxyHttps);
		}
	}

	/**
	 * <p>
	 * Habilita o proxy utilizado na rede, facilitando o manuseio de webservices interno (mesma rede) e externo
	 * (internet).
	 * </p>
	 * 
	 * @param hostProxyHttp
	 *        servidor proxy HTTP
	 * @param portProxyHttp
	 *        porta proxy HTTP
	 */
	public static void enable(String hostProxyHttp, String portProxyHttp) {
		if (StringUtils.isBlank(System.getProperty(SERVIDOR_PROXY_HTTP))) {
			System.setProperty(SERVIDOR_PROXY_HTTP, hostProxyHttp);
		}

		if (StringUtils.isBlank(System.getProperty(PORTA_PROXY_HTTP))) {
			System.setProperty(PORTA_PROXY_HTTP, portProxyHttp);
		}
	}

	/**
	 * <p>
	 * Desabilita o proxy utilizado na rede, tendo em vista que no manuseio de webservice na mesma rede não é
	 * necessário.
	 * </p>
	 */
	public static void disable() {
		System.clearProperty(SERVIDOR_PROXY_HTTP);
		System.clearProperty(PORTA_PROXY_HTTP);
		System.clearProperty(SERVIDOR_PROXY_HTTPS);
		System.clearProperty(PORTA_PROXY_HTTPS);
	}

}
