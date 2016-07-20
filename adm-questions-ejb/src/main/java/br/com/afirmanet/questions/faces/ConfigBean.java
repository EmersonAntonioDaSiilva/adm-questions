package br.com.afirmanet.questions.faces;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import br.com.afirmanet.core.util.StringUtils;

@Named
@SessionScoped
public class ConfigBean implements Serializable {

	private static final long serialVersionUID = 243720554312017323L;

	@Getter
	@Setter
	private String lastUrlBeforeLoggingIn;

	@Getter
	@Setter
	private boolean triedNotAuthenticatedAccess;

	public String redirectingToLastUrlBeforeLoggingIn() {
		String str = StringUtils.isBlank(lastUrlBeforeLoggingIn) ? "/home.xhtml" : lastUrlBeforeLoggingIn;
		lastUrlBeforeLoggingIn = null;
		triedNotAuthenticatedAccess = false;

		return str;
	}

}
