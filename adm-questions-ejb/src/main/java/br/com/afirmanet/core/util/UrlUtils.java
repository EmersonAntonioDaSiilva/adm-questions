package br.com.afirmanet.core.util;

import java.net.MalformedURLException;
import java.net.URL;

import br.com.afirmanet.core.exception.SystemException;

public class UrlUtils {

	public static URL getUrl(String stringRepresentation) {
		try {
			URL url = new URL(stringRepresentation);
			return url;

		} catch (MalformedURLException e) {
			throw new SystemException(e);
		}
	}

}
