package br.com.afirmanet.core.faces.validator;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import br.com.afirmanet.core.document.ie.Ie;
import br.com.afirmanet.core.document.ie.IeSaoPaulo;
import br.com.afirmanet.core.faces.Message;

@RequestScoped
@FacesValidator(IeValidator.VALIDATOR_ID)
@Slf4j
public class IeValidator implements Validator {

	public static final String VALIDATOR_ID = "IeValidator";
	public static final String INVALID_IE = "br.com.afirmanet.faces.validator.IeValidator.IE";

	@Inject
	private Message message;

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value == null) {
			return;
		}

		try {
			String ie = (String) value;

			Ie ieSaoPaulo = new IeSaoPaulo();
			if (!ieSaoPaulo.validate(ie)) {
				throw new ValidatorException(message.getFacesErrorMessages(INVALID_IE));
			}

		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new ValidatorException(message.getFacesErrorMessages(INVALID_IE));
		}
	}

}
