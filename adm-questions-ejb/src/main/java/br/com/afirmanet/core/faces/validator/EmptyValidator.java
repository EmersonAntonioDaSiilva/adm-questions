package br.com.afirmanet.core.faces.validator;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.com.afirmanet.core.faces.Message;

@RequestScoped
@FacesValidator(EmptyValidator.VALIDATOR_ID)
public class EmptyValidator implements Validator {

	public static final String VALIDATOR_ID = "EmptyValidator";
	public static final String EMPTY_VALUE = "br.com.afirmanet.faces.validator.EmptyValidator.Empty";

	@Inject
	private Message message;

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value == null || StringUtils.isEmpty(value.toString())) {
			throw new ValidatorException(message.getFacesErrorMessages(EMPTY_VALUE));
		}
	}

}
