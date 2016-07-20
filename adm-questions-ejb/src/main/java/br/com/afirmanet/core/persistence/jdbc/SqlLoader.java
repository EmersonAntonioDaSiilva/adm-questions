package br.com.afirmanet.core.persistence.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import lombok.extern.slf4j.Slf4j;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.com.afirmanet.core.persistence.jdbc.constant.DatabaseConstants;
import br.com.afirmanet.core.util.XmlUtils;

/**
 * <p>
 * Classe para carregar instruções SQL providos num arquivo(s) em notação XML.
 * </p>
 */
@Slf4j
public class SqlLoader {

	public static Map<String, String> load() {
		log.info("Loading SQL definitions from XML config files");

		Map<String, String> sqlMap = new HashMap<>();

		List<String> files = getSqlMappingFiles();

		for (String file : files) {
			log.info("Load file: " + file);
			sqlMap.putAll(loadSql(DatabaseConstants.DEFAULT_SQL_MAPPING_FOLDER + file));
		}

		return sqlMap;
	}

	/**
	 * <p>
	 * Método para leitura dos nomes de arquivos contendo as instruções SQL.
	 * </p>
	 * 
	 * @return List contendo os nomes dos arquivos que contém as instruções SQL.
	 */
	private static List<String> getSqlMappingFiles() {
		try {
			NodeList nodeList = XmlUtils.getNodeList(DatabaseConstants.DEFAULT_CONFIG_FILE, DatabaseConstants.XML_FILE_MAPPINGS_PATH);

			List<String> files = new ArrayList<>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				files.add(node.getLocalName());
			}

			return files;

		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	/**
	 * Méttodo para carregar as instruções SQL contidas no arquivo informado.
	 * 
	 * @param fileName
	 *        nome do arquivo contendo as instruções a serem carregadas.
	 * @return Map com as instruções contidas no arquivo informado.
	 */
	private static Map<String, String> loadSql(final String fileName) {
		try {
			Map<String, String> sqlFileMap = new HashMap<>();

			NodeList nodeList = XmlUtils.getNodeList(fileName, DatabaseConstants.XML_SQL_PATH);

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				String sqlName = node.getAttributes().getNamedItem(DatabaseConstants.SQL_NAME_ATTRIBUTE).getNodeValue();
				String sqlStatement = node.getLocalName();
				sqlFileMap.put(sqlName, sqlStatement);
			}

			return sqlFileMap;

		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

}
