package br.com.afirmanet.core.util;

import java.io.InputStream;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.exception.SystemException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesUtils {

	public static Properties load(final String propertiesFileName) {
		Properties properties = new Properties();
		InputStream inputStream = null;

		try {
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFileName);
			if (inputStream == null) {
				inputStream = PropertiesUtils.class.getResourceAsStream(propertiesFileName);
			}

			if (inputStream == null) {
				inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(propertiesFileName);
			}

			if (inputStream != null) {
				properties.load(inputStream);
				log.debug("Arquivo [{}] carregado com sucesso. Propriedades (key/value): \n{}", propertiesFileName, properties.toString());

			} else {
				throw new ApplicationException("Arquivo {} não pode ser encontrado.", propertiesFileName);
			}

		} catch (Throwable e) {
			throw new SystemException(e);

		} finally {
			IoUtils.closeQuietly(inputStream);
		}

		return properties;
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades.
	 * </p>
	 *
	 * @param properties
	 *        Arquivo de propriedades a ser utilizado para recuperação do valor.
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @return Valor referente à chave passada por parâmetro.
	 */
	public static String getValue(final Properties properties, final String key) {
		return properties.getProperty(key);
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades.
	 * </p>
	 *
	 * @param properties
	 *        Arquivo de propriedades a ser utilizado para recuperação do valor.
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @param defaultValue
	 *        Valor default caso não tenha sido definida a chave no arquivo de propriedades.
	 * @return Valor referente à chave passada por parâmetro ou o valor default.
	 */
	public static String getValue(final Properties properties, final String key, final String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades.
	 * </p>
	 *
	 * @param properties
	 *        Arquivo de propriedades a ser utilizado para recuperação do valor.
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @return Valor referente à chave passada por parâmetro.
	 */
	public static Integer getValueAsInteger(final Properties properties, final String key) {
		return Integer.valueOf(properties.getProperty(key));
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades.
	 * </p>
	 *
	 * @param properties
	 *        Arquivo de propriedades a ser utilizado para recuperação do valor.
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @param defaultValue
	 *        Valor default caso não tenha sido definida a chave no arquivo de propriedades.
	 * @return Valor referente à chave passada por parâmetro ou o valor default.
	 */
	public static Integer getValueAsInteger(final Properties properties, final String key, final String defaultValue) {
		return Integer.valueOf(properties.getProperty(key, defaultValue));
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades.
	 * </p>
	 *
	 * @param properties
	 *        Arquivo de propriedades a ser utilizado para recuperação do valor.
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @return Valor referente à chave passada por parâmetro.
	 */
	public static Long getValueAsLong(final Properties properties, final String key) {
		return Long.valueOf(properties.getProperty(key));
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades.
	 * </p>
	 *
	 * @param properties
	 *        Arquivo de propriedades a ser utilizado para recuperação do valor.
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @param defaultValue
	 *        Valor default caso não tenha sido definida a chave no arquivo de propriedades.
	 * @return Valor referente à chave passada por parâmetro ou o valor default.
	 */
	public static Long getValueAsLong(final Properties properties, final String key, final String defaultValue) {
		return Long.valueOf(properties.getProperty(key, defaultValue));
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades.
	 * </p>
	 *
	 * @param properties
	 *        Arquivo de propriedades a ser utilizado para recuperação do valor.
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @return Valor booleano referente à chave passada por parâmetro.
	 */
	public static Boolean getValueAsBoolean(final Properties properties, final String key) {
		return Boolean.valueOf(properties.getProperty(key));
	}

	/**
	 * <p>
	 * Obtém o valor da chave passada por parâmetro, cuja mesma deverá estar definida no arquivo de propriedades.
	 * </p>
	 *
	 * @param properties
	 *        Arquivo de propriedades a ser utilizado para recuperação do valor.
	 * @param key
	 *        Chave do arquivo de propriedade.
	 * @param defaultValue
	 *        Valor default caso não tenha sido definida a chave no arquivo de propriedades.
	 * @return Valor booleano referente à chave passada por parâmetro ou o valor default.
	 */
	public static Boolean getValueAsBoolean(final Properties properties, final String key, final String defaultValue) {
		return Boolean.valueOf(properties.getProperty(key, defaultValue));
	}

}
