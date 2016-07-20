package br.com.afirmanet.core.io.jatb.adapter;

import org.apache.commons.lang3.StringUtils;

public class StringAdapter extends TxtAdapter<String, String> {

	@Override
	public String marshal(String string) {
		if (string == null) {
			return null;
		}
		return string;
	}

	@Override
	public String unmarshal(String str) {
		if (str == null) {
			return null;
		}
		return str;
	}

	@Override
	public String marshalRemove(String string, String caracter) {
		if (string == null) {
			return null;
		}
		return StringUtils.remove(String.valueOf(string), caracter);
	}

}
