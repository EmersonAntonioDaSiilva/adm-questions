package br.com.afirmanet.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

import br.com.afirmanet.core.exception.SystemException;

import com.google.common.base.Splitter;

/**
 * <p>
 * Classe utilitária para manipulação <code>Enums</code>.
 * </p>
 *
 */
public final class EnumUtils extends org.apache.commons.lang3.EnumUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private EnumUtils() {
		super();
	}

	public static <E extends Enum<E>> E valueOf(Class<E> clazz, String propertyName, Object propertyValue) {
		Boolean isNumber = propertyValue instanceof Number;
		@SuppressWarnings("rawtypes")
		Class propertyValueClass = propertyValue.getClass();
		try {
			for (E e : clazz.getEnumConstants()) {
				Object o = null;
				String stringValue = BeanUtils.getProperty(e, propertyName);
				if (isNumber) {
					o = ConvertUtils.convert(stringValue, propertyValueClass);
				} else {
					o = stringValue;
				}

				if (o.equals(propertyValue)) {
					return e;
				}

			}
		} catch (Throwable e) {
			throw new SystemException(e);
		}

		throw new IllegalArgumentException(String.format("Não há contante definida para o campo %s e valor %s", propertyName, propertyValue));
	}

	public static <E extends Enum<E>> E valueOf(Class<E> enumClass, Integer id) {
		return valueOf(enumClass, "id", id);
	}

	public static <E> List<Integer> toList(List<E> enums) {
		return toList(enums, "id");
	}

	public static <E> List<Integer> toList(List<E> enums, String propertyName) {
		List<Integer> ids = new ArrayList<>();

		try {
			for (E e : enums) {
				ids.add((Integer) PropertyUtils.getProperty(e, propertyName));
			}

		} catch (Throwable e) {
			throw new SystemException(e);
		}

		return ids;
	}

	@SafeVarargs
	public static <E> List<Integer> toList(E... enums) {
		return toList("id", enums);
	}

	@SafeVarargs
	public static <E> List<Integer> toList(String propertyName, E... enums) {
		List<Integer> ids = new ArrayList<>();

		try {
			for (E e : enums) {
				ids.add((Integer) PropertyUtils.getProperty(e, propertyName));
			}

		} catch (Throwable e) {
			throw new SystemException(e);
		}

		return ids;
	}

	public static <E> List<String> toStringList(E[] enums) {
		return toStringList(Arrays.asList(enums));
	}

	public static <E> List<String> toStringList(List<E> enums) {
		List<String> strings = new ArrayList<>();

		for (E e : enums) {
			strings.add(e.toString());
		}

		return strings;
	}

	public static <E extends Enum<E>> List<E> parse(Class<E> enumClass, Iterable<String> value, String propertyName) {
		List<E> enums = new ArrayList<>();

		Iterator<String> iterator = value.iterator();
		while (iterator.hasNext()) {
			E enumm = valueOf(enumClass, propertyName, iterator.next());
			if (enumm != null) {
				enums.add(enumm);
			}
		}

		return enums;
	}

	public static <E extends Enum<E>> List<E> parse(Class<E> enumClass, String value, Character delimiter, String propertyName) {
		Iterable<String> iterable = Splitter.on(delimiter).omitEmptyStrings().trimResults().split(value);
		List<E> enums = parse(enumClass, iterable, propertyName);

		return enums;
	}

	public static <E extends Enum<E>> List<E> parse(Class<E> enumClass, String value) {
		List<E> enums = parse(enumClass, value, ';', "id");

		return enums;
	}

}
