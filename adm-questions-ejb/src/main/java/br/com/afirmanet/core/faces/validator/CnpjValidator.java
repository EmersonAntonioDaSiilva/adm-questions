package br.com.afirmanet.core.faces.validator;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.CnpjUtils;

@RequestScoped
@FacesValidator(CnpjValidator.VALIDATOR_ID)
@Slf4j
public class CnpjValidator implements Validator {

	public static final String VALIDATOR_ID = "EmptyValidator";
	public static final String INVALID_CNPJ = "br.com.afirmanet.faces.validator.CnpjValidator.CNPJ";

	@Inject
	private Message message;

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value == null) {
			return;
		}

		try {
			String cnpj = (String) value;
			if (!CnpjUtils.isValid(cnpj)) {
				throw new ValidatorException(message.getFacesErrorMessages(INVALID_CNPJ));
			}

		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new ValidatorException(message.getFacesErrorMessages(INVALID_CNPJ));
		}
	}

}
