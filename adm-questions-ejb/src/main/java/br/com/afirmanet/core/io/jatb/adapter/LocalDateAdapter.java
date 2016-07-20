package br.com.afirmanet.core.io.jatb.adapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.com.afirmanet.core.util.DateUtils;

public class LocalDateAdapter extends TxtAdapter<String, LocalDate> {

	@Override
	public String marshal(LocalDate date) {
		return DateUtils.format(date, DateUtils.DATE_PATTERN_YYYYMMDD);
	}

	public String marshal(LocalDate date, String pattern) {
		return DateUtils.format(date, pattern);
	}

	@Override
	public LocalDate unmarshal(String str) {
		return LocalDate.parse(str, DateTimeFormatter.ofPattern(DateUtils.DATE_PATTERN_YYYYMMDD));
	}

	@Override
	public String marshalRemove(LocalDate v, String caracter) {
		throw new UnsupportedOperationException("Não possui implementação!");
	}

}
