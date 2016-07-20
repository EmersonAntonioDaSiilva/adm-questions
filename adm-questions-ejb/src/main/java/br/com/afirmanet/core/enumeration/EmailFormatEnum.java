package br.com.afirmanet.core.enumeration;

import lombok.Getter;

public enum EmailFormatEnum {

	TEXT_PLAIN("text/plain"),
	TEXT_HTML("text/html");

	@Getter
	private String value;

	EmailFormatEnum(String value) {
		this.value = value;
	}

}
