package br.com.afirmanet.core.interceptor;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import lombok.extern.slf4j.Slf4j;
import br.com.afirmanet.core.exception.ApplicationException;

@Slf4j
@Message
@Interceptor
public class MessageInterceptor implements Serializable {

	private static final long serialVersionUID = 3377463775067057462L;

	@Inject
	private transient FacesContext facesContext;

	@AroundInvoke
	public Object addMessage(InvocationContext ctx) {
		try {
			return ctx.proceed();

		} catch (ApplicationException e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
			return null;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			String messageBundleName = facesContext.getApplication().getMessageBundle();
			String message = ResourceBundle.getBundle(messageBundleName).getString("br.com.afirmanet.faces.message.INTERNAL_ERROR");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, message, null));
			return null;
		}
	}

}
