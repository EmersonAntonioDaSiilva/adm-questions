package br.com.afirmanet.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import lombok.extern.slf4j.Slf4j;
import br.com.afirmanet.core.exception.SystemException;

/**
 * <p>
 * Classe utilitária para manipulação dos atributos presentes no arquivo MANIFEST-MF, possibilitando obter a versão da
 * aplicação, nome do sistema, etc...
 * </p>
 */
@Slf4j
public final class ManifestUtils {

	/**
	 * <p>
	 * Atributo que define o nome do sistema.
	 * </p>
	 */
	public static final String IMPLEMENTATION_TITLE = "Implementation-Title";

	/**
	 * <p>
	 * Atributo que define a versão do sistema.
	 * </p>
	 */
	public static final String IMPLEMENTATION_VERSION = "Implementation-Version";

	/**
	 * <p>
	 * Atributo que define o nome da empresa que desenvolveu o sistema.
	 * </p>
	 */
	public static final String IMPLEMENTATION_VENDOR = "Implementation-Vendor";

	/**
	 * <p>
	 * Atributo que define a estrutura de pacotes padrão do sistema. Ex: br.com.nomedaempresa
	 * </p>
	 */
	public static final String IMPLEMENTATION_VENDOR_ID = "Implementation-Vendor-Id";

	/**
	 * <p>
	 * Nome da pasta onde está localizado o arquivo <code>MANIFEST.MF</code>.
	 * </p>
	 */
	private static final String MANIFEST_FOLDER = "META-INF";

	/**
	 * <p>
	 * Nome do arquivo <code>MANIFEST.MF</code>, que detém os atributos informativos do sistema.
	 * </p>
	 */
	private static final String MANIFEST = "MANIFEST.MF";

	private static Manifest manifest;
	private static Attributes attributes;
	private static String context;

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private ManifestUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	/**
	 * <p>
	 * Carrega os atributos presentes no arquivo <b>MANIFEST.MF</b>.
	 * </p>
	 * 
	 * @throws SystemException
	 *         se ocorreu algum erro de I/O
	 */
	private static void loadManifestAttributes() {
		File container = new File(context);
		File manifestFolder = new File(container, MANIFEST_FOLDER);
		File manifestFile = new File(manifestFolder, MANIFEST);
		try (FileInputStream fileInputStream = new FileInputStream(manifestFile)) {
			manifest = new Manifest(fileInputStream);
			attributes = manifest.getMainAttributes();

		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * <p>
	 * Obtém o valor referente a chave do atributo passada por parâmetro.
	 * </p>
	 * 
	 * @param contextName
	 *        uma <code>String</code> que representa o nome do contexto
	 * @param attributeKey
	 *        uma <code>String</code> que representa a chave do atributo
	 * 
	 * @return valor do atributo
	 */
	public static synchronized String getValueAttribute(final String contextName, final String attributeKey) {
		String attributeValue = null;
		try {
			if (manifest == null) {
				context = contextName;
				loadManifestAttributes();
			}

			attributeValue = attributes.getValue(attributeKey);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}

		return attributeValue;
	}

}
