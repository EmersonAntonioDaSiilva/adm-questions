package br.com.afirmanet.core.faces;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import lombok.Getter;

@RequestScoped
public class Message implements Serializable {

	private static final long serialVersionUID = -5112766945862838845L;

	@Inject
	@Getter
	private FacesContext facesContext;

	public FacesMessage getFacesErrorMessages(final String key) {
		String msg = getText(key);
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
	}

	private String getText(final String key) {
		String messageBundleName = facesContext.getApplication().getMessageBundle();
		return ResourceBundle.getBundle(messageBundleName).getString(key);

	}

}
