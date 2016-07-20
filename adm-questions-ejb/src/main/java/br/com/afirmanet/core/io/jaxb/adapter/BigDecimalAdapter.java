package br.com.afirmanet.core.io.jaxb.adapter;

import java.math.BigDecimal;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import br.com.afirmanet.core.util.BigDecimalUtils;

public class BigDecimalAdapter extends XmlAdapter<String, BigDecimal> {

	@Override
	public String marshal(BigDecimal bigDecimal) {
		return BigDecimalUtils.format(bigDecimal, Locale.US, "##0.00");
	}

	@Override
	public BigDecimal unmarshal(String str) {
		return new BigDecimal(str);
	}

}
