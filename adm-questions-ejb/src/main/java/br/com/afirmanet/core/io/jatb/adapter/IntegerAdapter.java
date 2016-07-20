package br.com.afirmanet.core.io.jatb.adapter;

import org.apache.commons.lang3.StringUtils;

public class IntegerAdapter extends TxtAdapter<String, Integer> {

	@Override
	public String marshal(Integer integer) {
		if (integer == null) {
			return null;
		}

		String str = String.valueOf(integer);
		return str;
	}

	@Override
	public Integer unmarshal(String str) {
		if (str == null) {
			return null;
		}

		Integer integer = Integer.parseInt(str);
		return integer;
	}

	@Override
	public String marshalRemove(Integer integer, String caracter) {
		if (integer == null) {
			return null;
		}
		return StringUtils.remove(String.valueOf(integer), caracter);
	}

}
