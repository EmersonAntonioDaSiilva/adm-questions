package br.com.afirmanet.core.producer;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.com.afirmanet.core.qualifier.RequestParameter;

public class RequestParameterProducer {

	@Inject
	private FacesContext facesContext;

	@Produces
	@RequestParameter
	public String getRequestParameter(InjectionPoint ip) {
		String name = ip.getAnnotated().getAnnotation(RequestParameter.class).value();

		if ("".equals(name)) {
			name = ip.getMember().getName();
		}

		return facesContext.getExternalContext().getRequestParameterMap().get(name);
	}
}
