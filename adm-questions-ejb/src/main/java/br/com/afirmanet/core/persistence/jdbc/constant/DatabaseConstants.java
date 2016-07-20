package br.com.afirmanet.core.persistence.jdbc.constant;

/**
 * Classe utilitária contendo as constantes utilizadas na API de persistência.
 */
public final class DatabaseConstants {

	public static final String DEFAULT_SQL_MAPPING_FOLDER = "/META-INF/sql-mapping/";
	public static final String DEFAULT_CONFIG_FILE = "/META-INF/persistence.xml";

	public static final String XML_SQL_PATH = "/sql-mappings/sql";
	public static final String XML_FILE_MAPPINGS_PATH = "/config/file-mappings/file";
	public static final String DATA_SOURCE_PATH = "/config/data-source";

	public static final String SQL_NAME_ATTRIBUTE = "name";

	/**
	 * Classe utilitária não deve ter construtor público ou default.
	 */
	private DatabaseConstants() {
		// Classe utilitária não deve ter construtor público ou default.
	}

}
