package br.com.afirmanet.core.persistence.jdbc;

import java.sql.Connection;

import javax.persistence.PersistenceException;

import br.com.afirmanet.core.persistence.jdbc.constant.DatabaseConstants;
import br.com.afirmanet.core.util.XmlUtils;

/**
 * Classe fábrica de sessões da API de persistência.
 */
public class SqlSessionFactory { // SUPPRESS CHECKSTYLE AbstractClassName

	private static String dataSource;

	static {
		load();
	}

	private static void load() {
		try {
			dataSource = XmlUtils.getNodeValue(DatabaseConstants.DEFAULT_CONFIG_FILE, DatabaseConstants.DATA_SOURCE_PATH);
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	public static SqlSession getSession() {
		return new SqlSession(ConnectionFactory.getConnection(dataSource));
	}

	public static SqlSession getSession(Connection connection) {
		return new SqlSession(connection);
	}

}
