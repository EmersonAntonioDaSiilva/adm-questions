package br.com.afirmanet.core.io.jaxb.adapter;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import br.com.afirmanet.core.util.DateUtils;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

	@Override
	public String marshal(LocalDate date) {
		return DateUtils.format(date, DateUtils.DATE_PATTERN_YYYYMMDD);
	}

	@Override
	public LocalDate unmarshal(String str) {
		return DateUtils.parseLocalDate(str, DateUtils.DATE_PATTERN_YYYYMMDD);
	}

}
