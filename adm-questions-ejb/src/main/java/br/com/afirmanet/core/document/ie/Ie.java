package br.com.afirmanet.core.document.ie;


public interface Ie {

	boolean isLegible(final String document);

	boolean isFormatted(final String document);

	String getCheckDigits(final String document);

	String format(final String document);

	boolean validate(final String document);

}
