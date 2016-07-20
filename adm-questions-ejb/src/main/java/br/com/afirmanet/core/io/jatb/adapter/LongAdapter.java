package br.com.afirmanet.core.io.jatb.adapter;

import org.apache.commons.lang3.StringUtils;

public class LongAdapter extends TxtAdapter<String, Long> {

	@Override
	public String marshal(Long number) {
		if (number == null) {
			return null;
		}

		String str = String.valueOf(number);
		return str;
	}

	@Override
	public Long unmarshal(String str) {
		if (str == null) {
			return null;
		}

		Long number = Long.parseLong(str);
		return number;
	}

	@Override
	public String marshalRemove(Long value, String caracter) {
		return StringUtils.remove(value.toString(), caracter);
	}

}
