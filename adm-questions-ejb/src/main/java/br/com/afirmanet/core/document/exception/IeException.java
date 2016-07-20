package br.com.afirmanet.core.document.exception;

import java.text.MessageFormat;

public class IeException extends RuntimeException {

	private static final long serialVersionUID = -5533082382440214450L;

	public IeException() {
		super();
	}

	public IeException(String message) {
		super(message);
	}

	public IeException(String pattern, Object... arguments) {
		super(MessageFormat.format(pattern, arguments));
	}

	public IeException(Throwable cause) {
		super(cause);
	}

}
