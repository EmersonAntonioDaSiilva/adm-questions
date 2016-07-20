package br.com.afirmanet.core.io.jatb.adapter;

import java.math.BigDecimal;

import br.com.afirmanet.core.util.StringUtils;

public class BigDecimalAdapter extends TxtAdapter<String, BigDecimal> {

	@Override
	public String marshal(BigDecimal bigDecimal) {
		if (bigDecimal == null) {
			return StringUtils.EMPTY;
		}

		return StringUtils.removeNonDigits(bigDecimal.toString());
	}

	@Override
	public BigDecimal unmarshal(String str) {
		if (str == null) {
			return null;
		}

		BigDecimal bigDecimal = new BigDecimal(String.format("%s.%s", str.substring(0, str.length() - 2), str.substring(str.length() - 2)));
		return bigDecimal;
	}

	@Override
	public String marshalRemove(BigDecimal value, String caracter) {
		String valueAdapted = value == null ? "0" : value.toString();
		if (valueAdapted.equals("0")) {
			return "0";
		}

		String[] vector = valueAdapted.split(caracter);
		if (vector.length == 1) {
			return vector[0] + "00";
		}

		String decimal = vector[1];
		if (vector[1].length() == 1) {
			decimal = vector[1] + "0";
		}
		return valueAdapted = vector[0] + decimal;
	}

}
