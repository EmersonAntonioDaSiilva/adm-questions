package br.com.afirmanet.questions.utils;

import java.util.Properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Classe utilitária para manipulação dos valores existentes no arquivo de propriedade <i>application.properties</i>
 * utilizado para definir as configurações padrão do sistema.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationPropertiesUtils {

	private static Properties properties;

	static {
		properties = PropertiesUtils.load("application.properties");
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades
	 * utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @return Valor referente à chave passada por parâmetro.
	 */
	public static String getValue(String key) {
		return PropertiesUtils.getValue(properties, key);
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades
	 * utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @param defaultValue
	 *        Valor default caso não tenha sido definida a chave no arquivo de propriedades.
	 * @return Valor referente à chave passada por parâmetro ou o valor default.
	 */
	public static String getValue(String key, String defaultValue) {
		return PropertiesUtils.getValue(properties, key, defaultValue);
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades
	 * utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @return Valor referente à chave passada por parâmetro.
	 */
	public static Integer getValueAsInteger(String key) {
		return PropertiesUtils.getValueAsInteger(properties, key);
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades
	 * utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @param defaultValue
	 *        Valor default caso não tenha sido definida a chave no arquivo de propriedades.
	 * @return Valor referente à chave passada por parâmetro ou o valor default.
	 */
	public static Integer getValueAsInteger(String key, String defaultValue) {
		return PropertiesUtils.getValueAsInteger(properties, key, defaultValue);
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades
	 * utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @return Valor referente à chave passada por parâmetro.
	 */
	public static Long getValueAsLong(String key) {
		return PropertiesUtils.getValueAsLong(properties, key);
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades
	 * utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @param defaultValue
	 *        Valor default caso não tenha sido definida a chave no arquivo de propriedades.
	 * @return Valor referente à chave passada por parâmetro ou o valor default.
	 */
	public static Long getValueAsLong(String key, String defaultValue) {
		return PropertiesUtils.getValueAsLong(properties, key, defaultValue);
	}
	
	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades
	 * utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @return Valor referente à chave passada por parâmetro.
	 */	
	public static Double getValueAsDouble(String key) {
		return PropertiesUtils.getValueAsDouble(properties, key);
	}
	
	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades
	 * utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @param defaultValue
	 *        Valor default caso não tenha sido definida a chave no arquivo de propriedades.
	 * @return Valor referente à chave passada por parâmetro ou o valor default.
	 */
	public static Double getValueAsDouble(String key, String defaultValue) {
		return PropertiesUtils.getValueAsDouble(properties, key, defaultValue);
	}
	

	/**
	 * <p>
	 * Obtém o valor booleano da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de
	 * propriedades utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @return Valor booleano referente à chave passada por parâmetro.
	 */
	public static Boolean getValueAsBoolean(String key) {
		return PropertiesUtils.getValueAsBoolean(properties, key);
	}

	/**
	 * <p>
	 * Obtém o valor booleano da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de
	 * propriedades utilizado para configuração do sistema.
	 * </p>
	 * 
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @param defaultValue
	 *        Valor default caso não tenha sido definida a chave no arquivo de propriedades.
	 * @return Valor booleano referente à chave passada por parâmetro ou o valor default.
	 */
	public static Boolean getValueAsBoolean(String key, String defaultValue) {
		return PropertiesUtils.getValueAsBoolean(properties, key, defaultValue);
	}
}
