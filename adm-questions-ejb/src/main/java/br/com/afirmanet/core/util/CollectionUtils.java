package br.com.afirmanet.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

/**
 * <p>
 * Classe utilitária para manipulação de {@link Collection}.
 * </p>
 *
 */
public final class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

	public static <T, U, R> Collection<ArrayList<R>> groupToArrayList(Collection<T> from, Function<T, U> groupingFunction, Function<T, R> extractionFunction) {
		Collection<ArrayList<R>> collection = new ArrayList<>();

		Map<U, List<T>> grouped = from.stream().collect(Collectors.groupingBy(groupingFunction));
		for (List<T> groupingValue : grouped.values()) {
			collection.add(Lists.newArrayList(groupingValue.stream().map(extractionFunction).collect(Collectors.toList())));
		}

		return collection;
	}

}
