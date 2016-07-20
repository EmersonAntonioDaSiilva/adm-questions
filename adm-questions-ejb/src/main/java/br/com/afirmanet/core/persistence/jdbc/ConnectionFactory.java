package br.com.afirmanet.core.persistence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * Classe utilitária que atua como fábrica de classes de implementação da interface ConnectionManager.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionFactory { // SUPPRESS CHECKSTYLE AbstractClassName

	/**
	 * <p>
	 * Recupera uma conexão obtida através do datasource passado por parâmetro.
	 * </p>
	 * 
	 * @param datasourceName
	 *        Nome do datasource que se deseja obter conexão.
	 * 
	 * @return Conexão com a base de dados configurada no datasource passado por Parâmetro.
	 */
	public static Connection getConnection(final String datasourceName) {
		try {
			InitialContext initialContext = new InitialContext();
			DataSource dataSource = (DataSource) initialContext.lookup(datasourceName);

			if (dataSource == null) {
				throw new PersistenceException("Ocorreu um erro ao conectar com o pool de conexão para buscar o datasource.");
			}

			return dataSource.getConnection();

		} catch (NamingException e) {
			throw new PersistenceException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	public static Connection getConnection(final String driver, final String url, final String user, final String password) {
		try {
			Class.forName(driver);
			return DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new PersistenceException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	public static Connection getConnection(final String url, final String user, final String password) {
		return getConnection("oracle.jdbc.driver.OracleDriver", url, user, password);
	}

}