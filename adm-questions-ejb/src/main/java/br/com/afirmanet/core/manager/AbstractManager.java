package br.com.afirmanet.core.manager;

import java.util.Collection;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractManager {

	@Inject
	@Getter
	private FacesContext facesContext;

	protected void addInfoMessage(String message, Object... params) {
		addMessage(FacesMessage.SEVERITY_INFO, message, params);
	}

	protected void addInfoMessageFromResourceBundle(String key, Object... params) {
		addMessageFromResourceBundle(FacesMessage.SEVERITY_INFO, key, params);
	}

	protected void addWarnMessage(String message, Object... params) {
		addMessage(FacesMessage.SEVERITY_WARN, message, params);
	}

	protected void addWarnMessageFromResourceBundle(String key, Object... params) {
		addMessageFromResourceBundle(FacesMessage.SEVERITY_WARN, key, params);
	}

	protected void addErrorMessage(String message, Object... params) {
		addMessage(FacesMessage.SEVERITY_ERROR, message, params);
	}

	protected void addErrorMessage(Exception exception) {
		addErrorMessage(exception.getMessage());
	}

	protected void addErrorMessageFromResourceBundle(String key, Object... params) {
		addMessageFromResourceBundle(FacesMessage.SEVERITY_ERROR, key, params);
	}

	protected void addFatalErrorMessage(Throwable exception) {
		log.error(exception.getMessage(), exception);
		addMessageFromResourceBundle(FacesMessage.SEVERITY_FATAL, "br.com.afirmanet.faces.message.INTERNAL_ERROR");
	}

	protected void addMessage(Severity severity, String message, Object... params) {
		for (Object param : params) {
			message = message.replaceFirst("\\{\\}", param.toString());
		}

		facesContext.addMessage(null, new FacesMessage(severity, message, null));
	}

	protected void addMessages(Collection<FacesMessage> facesMessages) {
		for (FacesMessage facesMessage : facesMessages) {
			facesContext.addMessage(null, facesMessage);
		}
	}

	private void addMessageFromResourceBundle(Severity severity, String key, Object... params) {
		String messageBundleName = facesContext.getApplication().getMessageBundle();
		String message = ResourceBundle.getBundle(messageBundleName).getString(key);
		addMessage(severity, message, params);
	}

	/*
	 * OBTÉM OS OBJETOS DAS REQUISIÇÕES E CONTEXTO
	 */

	public HttpServletRequest getRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}

	public ExternalContext getExternalContext() {
		return facesContext.getExternalContext();
	}

	public HttpServletResponse getResponse() {
		return (HttpServletResponse) getExternalContext().getResponse();
	}

	public String raizContextPath(){
		HttpServletRequest req = getRequest();
		StringBuffer url =  new StringBuffer();
		
		String scheme = req.getScheme();             // http
	    String serverName = req.getServerName();     // hostname.com
	    int serverPort = req.getServerPort();        // 80	
	    String contextPath = req.getContextPath();   // /mywebapp
	    
	    url.append(scheme).append("://").append(serverName);

	    if ((serverPort != 80) && (serverPort != 443)) {
	        url.append(":").append(serverPort);
	    }

	    url.append(contextPath);
	    
	    return url.toString();
	    
	}
	
	public Application getApplication() {
		return facesContext.getApplication();
	}

}