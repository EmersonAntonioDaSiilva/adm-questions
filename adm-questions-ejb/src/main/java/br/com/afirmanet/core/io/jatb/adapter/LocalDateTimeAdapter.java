package br.com.afirmanet.core.io.jatb.adapter;

import java.time.LocalDateTime;

import br.com.afirmanet.core.util.DateUtils;

public class LocalDateTimeAdapter extends TxtAdapter<String, LocalDateTime> {

	private static final String DATE_PATTERN_DDMMYYYYHHMMSS = "ddMMyyyyHHmmss";

	@Override
	public String marshal(LocalDateTime date) {
		return DateUtils.format(date, DATE_PATTERN_DDMMYYYYHHMMSS);
	}

	public String marshal(LocalDateTime date, String pattern) {
		return DateUtils.format(date, pattern);
	}

	@Override
	public LocalDateTime unmarshal(String str) {
		return DateUtils.parseLocalDateTime(str, DATE_PATTERN_DDMMYYYYHHMMSS);
	}

	@Override
	public String marshalRemove(LocalDateTime v, String caracter) {
		throw new UnsupportedOperationException("Não possui implementação!");
	}

}
