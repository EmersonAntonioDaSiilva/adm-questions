package br.com.afirmanet.core.io.jatb.exception;

import java.text.MessageFormat;

import lombok.Getter;

public class JatbLayoutException extends RuntimeException {

	private static final long serialVersionUID = 3025894029456287160L;

	@Getter
	private String code;

	public JatbLayoutException(Throwable cause) {
		super(cause);
	}

	public JatbLayoutException(Throwable cause, String format, Object... args) {
		super(new MessageFormat(format).format(args), cause);
	}

	public JatbLayoutException(String format, Object... args) {
		super(new MessageFormat(format).format(args));
	}

	public JatbLayoutException(String code, String format, Object... args) {
		this(format, args);
		this.code = code;
	}

}
