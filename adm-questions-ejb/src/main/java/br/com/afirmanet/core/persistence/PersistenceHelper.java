package br.com.afirmanet.core.persistence;

import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import br.com.afirmanet.core.util.ReflectionUtils;

public final class PersistenceHelper {

	private static Map<Class<?>, Integer> sqlTypes;
	private static List<Class<?>> validTypes;
	private static List<Class<?>> invalidTypes;

	static {
		// SQL Types
		sqlTypes = new HashMap<>();
		sqlTypes.put(String.class, Types.VARCHAR);
		sqlTypes.put(Integer.class, Types.INTEGER);
		sqlTypes.put(Long.class, Types.INTEGER);
		sqlTypes.put(java.time.LocalDate.class, Types.DATE);
		sqlTypes.put(java.time.LocalDateTime.class, Types.TIMESTAMP);
		sqlTypes.put(java.math.BigDecimal.class, Types.DECIMAL);
		sqlTypes.put(Boolean.class, Types.BOOLEAN);
		sqlTypes.put(java.sql.Date.class, Types.DATE);
		sqlTypes.put(Timestamp.class, Types.TIMESTAMP);
		sqlTypes.put(Character.class, Types.CHAR);
		sqlTypes.put(Time.class, Types.TIME);
		sqlTypes.put(Float.class, Types.FLOAT);
		sqlTypes.put(Double.class, Types.DOUBLE);
		sqlTypes.put(Byte.class, Types.INTEGER);
		sqlTypes.put(Short.class, Types.INTEGER);
		sqlTypes.put(java.math.BigInteger.class, Types.BIGINT);
		sqlTypes.put(byte[].class, Types.VARBINARY);
		sqlTypes.put(Clob.class, Types.CLOB);
		sqlTypes.put(Blob.class, Types.BLOB);
		sqlTypes.put(java.sql.Array.class, Types.ARRAY);

		// Valid Types
		validTypes = new ArrayList<>();
		validTypes.add(Byte.class);
		validTypes.add(Short.class);
		validTypes.add(Integer.class);
		validTypes.add(Long.class);
		validTypes.add(Float.class);
		validTypes.add(Double.class);
		validTypes.add(Number.class);
		validTypes.add(Boolean.class);
		validTypes.add(Character.class);
		validTypes.add(LocalDate.class);
		validTypes.add(LocalDateTime.class);
		validTypes.add(BigDecimal.class);
		validTypes.add(BigInteger.class);
		validTypes.add(Enum.class);

		// Invalid Types
		invalidTypes = new ArrayList<>();
		invalidTypes.add(Collection.class);
		invalidTypes.add(List.class);
		invalidTypes.add(AbstractList.class);
		invalidTypes.add(ArrayList.class);
		invalidTypes.add(LinkedList.class);
		invalidTypes.add(Set.class);
		invalidTypes.add(SortedSet.class);
		invalidTypes.add(AbstractSet.class);
		invalidTypes.add(HashSet.class);
		invalidTypes.add(LinkedHashSet.class);
		invalidTypes.add(TreeSet.class);
		invalidTypes.add(Pair.class);
	}

	public static void setValue(PreparedStatement statement, final int index, final Object value, final Class<?> type) { // SUPPRESS CHECKSTYLE JavaNCSS
		try {
			if (String.class.equals(type)) {
				statement.setString(index, value.toString());

			} else if (Integer.class.equals(type)) {
				statement.setInt(index, (Integer) value);

			} else if (Long.class.equals(type)) {
				statement.setLong(index, (Long) value);

			} else if (java.time.LocalDate.class.equals(type)) {
				statement.setDate(index, java.sql.Date.valueOf((java.time.LocalDate) value));

			} else if (java.time.LocalDateTime.class.equals(type)) {
				statement.setTimestamp(index, java.sql.Timestamp.valueOf((java.time.LocalDateTime) value));

			} else if (BigDecimal.class.equals(type)) {
				statement.setBigDecimal(index, (BigDecimal) value);

			} else if (Boolean.class.equals(type)) {
				statement.setBoolean(index, (Boolean) value);

			} else if (java.sql.Date.class.equals(type)) {
				statement.setDate(index, (java.sql.Date) value);

			} else if (java.sql.Timestamp.class.equals(type)) {
				statement.setTimestamp(index, (Timestamp) value);

			} else if (Character.class.equals(type)) {
				statement.setString(index, value.toString());

			} else if (Time.class.equals(type)) {
				statement.setTime(index, (Time) value);

			} else if (Float.class.equals(type)) {
				statement.setFloat(index, (Float) value);

			} else if (Double.class.equals(type)) {
				statement.setDouble(index, (Double) value);

			} else if (Byte.class.equals(type)) {
				statement.setByte(index, (Byte) value);

			} else if (Short.class.equals(type)) {
				statement.setShort(index, (Short) value);

			} else if (BigInteger.class.equals(type)) {
				statement.setLong(index, ((BigInteger) value).longValue());

			} else if (byte[].class.equals(type)) {
				statement.setBytes(index, (byte[]) value);

			} else if (Clob.class.equals(type)) {
				statement.setClob(index, (Clob) value);

			} else if (Blob.class.equals(type)) {
				statement.setBlob(index, (Blob) value);

			} else if (File.class.equals(type)) {
				File file = (File) value;

				try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
					statement.setBinaryStream(index, bufferedInputStream, file.length());
				}

			} else if (Array.class.equals(type)) {
				statement.setNull(index, Types.ARRAY);
			}

		} catch (SQLException | IOException e) {
			throw new PersistenceException(e);
		}
	}

	public static void setNullValue(PreparedStatement statement, final int index, final Class<?> type) {
		try {
			statement.setNull(index, sqlTypes.get(type));
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	public static boolean isValidType(final PropertyDescriptor property, final Object searchParam) {
		boolean validType = false;

		try {
			Class<?> propertyType = property.getPropertyType();

			if (validTypes.contains(propertyType) || propertyType.isEnum()) {
				validType = true;

			} else if (invalidTypes.contains(propertyType)) {
				validType = false;

			} else {

				String objectMethodName = "get" + StringUtils.capitalize(property.getName());
				Method objectMethod = searchParam.getClass().getMethod(objectMethodName, new Class[0]);
				Object object = objectMethod.invoke(searchParam, new Object[0]);

				String idMethodName = "get" + StringUtils.capitalize(ReflectionUtils.findIdColumnName(propertyType));
				Method idMethod = propertyType.getMethod(idMethodName, new Class[0]);
				Object id = idMethod.invoke(object, new Object[0]);

				if (id != null) {
					validType = true;
				}
			}

		} catch (Throwable e) {
			throw new PersistenceException(e);
		}

		return validType;
	}

}
