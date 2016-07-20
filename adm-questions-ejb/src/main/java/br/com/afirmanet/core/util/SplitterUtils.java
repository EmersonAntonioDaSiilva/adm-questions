package br.com.afirmanet.core.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;

public final class SplitterUtils {

	public static List<Integer> asIntegerList(String source, String separator) {
		if (StringUtils.isBlank(source)) {
			return new ArrayList<>();
		}

		List<Integer> list = new ArrayList<>();
		Iterator<String> iterator = Splitter.on(separator).omitEmptyStrings().trimResults().split(source).iterator();
		while (iterator.hasNext()) {
			list.add(Integer.valueOf(iterator.next()));
		}

		return list;
	}

	public static List<Integer> asIntegerList(String source) {
		return asIntegerList(source, ";");
	}

	public static List<Long> asLongList(String source, String separator) {
		if (StringUtils.isBlank(source)) {
			return new ArrayList<>();
		}

		List<Long> list = new ArrayList<>();
		Iterator<String> iterator = Splitter.on(separator).omitEmptyStrings().trimResults().split(source).iterator();
		while (iterator.hasNext()) {
			list.add(Long.valueOf(iterator.next()));
		}

		return list;
	}

	public static List<Long> asLongList(String source) {
		return asLongList(source, ";");
	}

	public static List<String> asStringList(String source, String separator) {
		if (StringUtils.isBlank(source)) {
			return new ArrayList<>();
		}

		List<String> list = new ArrayList<>();
		Iterator<String> iterator = Splitter.on(separator).omitEmptyStrings().trimResults().split(source).iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}

		return list;
	}

	public static List<String> asStringList(String source) {
		return asStringList(source, ";");
	}

	public static Map<String, LocalDate> asLocalDateMap(String source, String valuePairSeparator, String separator) {
		if (StringUtils.isBlank(source)) {
			return new HashMap<>();
		}

		Map<String, LocalDate> map = new HashMap<>();
		Iterator<String> iterator = Splitter.on(separator).omitEmptyStrings().trimResults().split(source).iterator();
		while (iterator.hasNext()) {
			String[] split = iterator.next().split(valuePairSeparator);
			String key = split[0];
			LocalDate date = DateUtils.parseLocalDate(split[1]);

			map.put(key, date);
		}

		return map;
	}

	public static Map<String, LocalDate> asLocalDateMap(String source) {
		return asLocalDateMap(source, "=", ";");
	}

}
