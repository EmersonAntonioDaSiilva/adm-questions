package br.com.afirmanet.core.io.filter;

import static org.hamcrest.Matchers.containsString;

import java.io.File;
import java.io.FilenameFilter;

import org.hamcrest.Matcher;

public class ContainsFilter implements FilenameFilter {
	private Matcher<String> matcher;

	public ContainsFilter(String param) {
		matcher = containsString(param);
	}

	@Override
	public boolean accept(File dir, String name) {
		return matcher.matches(name);
	}

}
