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
import br.com.afirmanet.core.util.CpfUtils;

@RequestScoped
@FacesValidator(CpfValidator.VALIDATOR_ID)
@Slf4j
public class CpfValidator implements Validator {

	public static final String VALIDATOR_ID = "CPFValidator";
	public static final String INVALID_CPF = "br.com.afirmanet.faces.validator.CpfValidator.CPF";

	@Inject
	private Message message;

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value == null) {
			return;
		}

		try {
			String cpf = (String) value;
			if (!CpfUtils.isValid(cpf)) {
				throw new ValidatorException(message.getFacesErrorMessages(INVALID_CPF));
			}

		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new ValidatorException(message.getFacesErrorMessages(INVALID_CPF));
		}
	}

}
