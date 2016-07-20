package br.com.afirmanet.core.persistence.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.tuple.Pair;

import br.com.afirmanet.core.persistence.PersistenceHelper;

/**
 * <p>
 * Classe que abstrai a seção de conversação entre a API de persistência e o SQL-Database.
 * </p>
 */
@Slf4j
public class SqlSession implements AutoCloseable {

	private static Map<String, String> sqlMap;

	static {
		sqlMap = SqlLoader.load();
	}

	@Getter
	@Setter
	private Connection connection;

	@Getter
	@Setter
	private PreparedStatement preparedStatement;

	public SqlSession(Connection connection) {
		this.connection = connection;
	}

	public ResultSet executeQueryStatement(String statement, List<? extends Object> parameterList, Integer fetchSize) {
		try {
			log.debug(getSqlStatement(statement, parameterList));

			preparedStatement = connection.prepareStatement(statement);
			if (fetchSize != null) {
				preparedStatement.setFetchSize(fetchSize);
			}

			setParameters(preparedStatement, parameterList);

			return preparedStatement.executeQuery();

		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	public ResultSet executeQueryStatement(String statement, List<? extends Object> parameterList) {
		return executeQueryStatement(statement, parameterList, null);
	}

	public ResultSet executeQueryStatement(String statement) {
		return executeQueryStatement(statement, new ArrayList<>(), null);
	}

	/**
	 * <p>
	 * Método para execução de consultas que no SQL-Database. Os parâmetros da são passados num List.
	 * </p>
	 *
	 * @param parameterList
	 *        lista de parâmetros
	 * @param sqlName
	 *        nome da query definida no arquivo XML
	 * @param fetchSize
	 *        fetchSize utilizado no PreparedStatement
	 * @return ResultSet resultado da consulta executada
	 */
	public ResultSet executeQuery(String sqlName, List<? extends Object> parameterList, Integer fetchSize) {
		return executeQueryStatement(getSqlStatement(sqlName), parameterList, fetchSize);
	}

	public ResultSet executeQuery(String sqlName, List<? extends Object> parameterList) {
		return executeQuery(sqlName, parameterList, null);
	}

	public ResultSet executeQuery(String sqlName) {
		return executeQuery(sqlName, new ArrayList<>(), null);
	}

	/**
	 * <p>
	 * Método para execução de instruções insert, Update e Delete no SQL-Database. Os parâmetros da são passados num
	 * List.
	 * </p>
	 *
	 * @param sqlName
	 *        nome da query definida no arquivo XML
	 *
	 * @param parameterList
	 *        lista de parâmetros
	 */
	public void executeUpdate(String sqlName, List<? extends Object> parameterList) {
		try {
			log.debug(getSqlStatement(sqlName, parameterList));

			preparedStatement = connection.prepareStatement(getSqlStatement(sqlName));
			setParameters(preparedStatement, parameterList);
			preparedStatement.executeUpdate();
			preparedStatement.close();

		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	public void executeBatchUpdate(String sqlName, List<? extends Object> parameterList, int maxNumberStatement) {
		try {
			log.debug(getSqlStatement(sqlName, parameterList));

			preparedStatement = connection.prepareStatement(getSqlStatement(sqlName));

			int counter = 0;
			for (Object object : parameterList) {

				Object[] values = (Object[]) object;
				int index = 1;
				for (Object value : values) {
					if (value != null) {
						setParameter(preparedStatement, index, value, value.getClass());
						index++;
					}
				}

				preparedStatement.addBatch();
				counter++;
				if (counter == maxNumberStatement) {
					preparedStatement.executeBatch();
					preparedStatement.clearBatch();
					counter = 0;
				}
			}
			preparedStatement.executeBatch();
			preparedStatement.clearBatch();
			preparedStatement.close();

		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e);
		}

	}

	public void executeBatch(String sqlName, ResultSet resultSet, List<? extends Object> parameterList, int maxNumberStatement) {
		try {
			log.debug(getSqlStatement(sqlName));

			preparedStatement = connection.prepareStatement(getSqlStatement(sqlName));

			int counter = 0;
			while (resultSet.next()) {
				addBatch(resultSet, parameterList);

				counter++;
				if (counter == maxNumberStatement) {
					preparedStatement.executeBatch();
					preparedStatement.clearBatch();
					counter = 0;
				}
			}
			preparedStatement.executeBatch();
			preparedStatement.clearBatch();

		} catch (SQLException e) {
			StringBuilder msg = new StringBuilder(e.getMessage());
			while ((e = e.getNextException()) != null) { //NOPMD
				msg.append("\n");
				msg.append(e.getMessage());
			}
			throw new PersistenceException(msg.toString()); //NOPMD
		}
	}

	public void executeBatch(String sqlName, ResultSet resultSet, List<? extends Object> parameterList) {
		executeBatch(sqlName, resultSet, parameterList, Integer.MAX_VALUE);
	}

	public void executeBatch(String sqlName, ResultSet resultSet, int maxNumberStatement) {
		executeBatch(sqlName, resultSet, new ArrayList<>(), maxNumberStatement);
	}

	public void executeBatch(String sqlName, ResultSet resultSet) {
		executeBatch(sqlName, resultSet, new ArrayList<>());
	}

	public String getSqlStatement(String sqlName) {
		String sqlStatement = sqlMap.get(sqlName);

		if (sqlStatement == null) {
			throw new PersistenceException("Não foi possível encontrar a instrução do SQL: " + sqlName);
		}

		return sqlStatement;
	}

	public Pair<String, List<String>> getSql(String sqlName) {
		String sqlStatement = getSqlStatement(sqlName);

		Pattern pattern = Pattern.compile(":(\\w)*");
		Matcher matcher = pattern.matcher(sqlStatement);

		List<String> fieldNames = new ArrayList<>();
		while (matcher.find()) {
			String field = matcher.group();
			sqlStatement = sqlStatement.replaceFirst(field, "?");
			fieldNames.add(field.substring(1));
		}

		return Pair.of(sqlStatement, fieldNames);
	}

	public void setParameter(PreparedStatement preparedStatement, Object parameterObject, List<String> fieldNames) {
		try {
			int index = 1;
			for (String fieldName : fieldNames) {
				Class<?> type = PropertyUtils.getPropertyType(parameterObject, fieldName);
				Object value = PropertyUtils.getProperty(parameterObject, fieldName);

				setParameter(preparedStatement, index++, value, type);
			}

		} catch (IllegalAccessException e) {
			throw new PersistenceException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new PersistenceException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	private void addBatch(ResultSet resultSet, List<? extends Object> parameterList) {
		try {
			setParameters(preparedStatement, resultSet);
			setParameters(preparedStatement, parameterList, resultSet.getMetaData().getColumnCount() + 1);
			preparedStatement.addBatch();

		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	private void setParameters(PreparedStatement preparedStatement, ResultSet resultSet) {
		try {
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				Object column = resultSet.getObject(i);
				if (column == null) {
					preparedStatement.setNull(i, resultSet.getMetaData().getColumnType(i));
				} else {
					preparedStatement.setObject(i, column);
				}
			}

		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	private void setParameters(PreparedStatement preparedStatement, List<? extends Object> parameterList, int index) {
		for (Object parameter : parameterList) {
			setParameter(preparedStatement, index++, parameter, parameter.getClass());
		}
	}

	private void setParameters(PreparedStatement preparedStatement, List<? extends Object> parameterList) {
		setParameters(preparedStatement, parameterList, 1);
	}

	private void setParameter(PreparedStatement preparedStatement, int index, Object value, Class<?> type) {
		if (value != null) {
			PersistenceHelper.setValue(preparedStatement, index, value, type);
		} else {
			PersistenceHelper.setNullValue(preparedStatement, index, type);
		}
	}

	/**
	 * <p>
	 * Retorna a instrução SQL associada ao identificador passado como parâmetro com seus respectivos parâmetros, ou
	 * seja, substitui o caracter <b>'?'</b> pelo valor de seu respectivo parâmetro, conforme lista de parâmetros
	 * fornecida.
	 * </p>
	 *
	 * @param sqlName
	 *        Identificador da SQL que se deseja obter a instrução.
	 * @param parameterList
	 *        Lista de parâmetros a serem adicionados a instrução SQL.
	 * @return Instrução SQL associada ao identificador passado como parâmetro com seus respectivos parâmetros.
	 */
	private String getSqlStatement(String sqlStatement, List<? extends Object> parameterList) {
		try {
			for (Object item : parameterList) {
				Object parameter = item;
				if (item instanceof java.time.LocalDate || item instanceof java.time.LocalDateTime) {
					parameter = "'" + item.toString() + "'";
				}

				if (item instanceof java.lang.String) {
					parameter = "'" + item.toString() + "'";
				}
				sqlStatement = sqlStatement.replaceFirst("\\?", parameter != null ? parameter.toString() : "NULL");
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return sqlStatement;
	}

	@Override
	public void close() {
		try {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		} catch (SQLException e) {
			log.warn(e.getMessage(), e);
		}

		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			log.warn(e.getMessage(), e);
		}
	}

}
