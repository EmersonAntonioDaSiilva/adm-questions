package br.com.afirmanet.core.io.jatb.exception;

import java.text.MessageFormat;
import java.util.Map;

import lombok.Getter;

public class JatbValidatorException extends RuntimeException {

	private static final long serialVersionUID = -1468898890572872635L;

	@Getter
	private String code;

	@Getter
	private Map<String, String> errors;

	public JatbValidatorException(Throwable cause) {
		super(cause);
	}

	public JatbValidatorException(Throwable cause, String format, Object... args) {
		super(new MessageFormat(format).format(args), cause);
	}

	public JatbValidatorException(String format, Object... args) {
		super(new MessageFormat(format).format(args));
	}

	public JatbValidatorException(String code, String format, Object... args) {
		this(format, args);
		this.code = code;
	}

	public JatbValidatorException(Map<String, String> errors, String format, Object... args) {
		this(format, args);
		this.errors = errors;
	}

}
