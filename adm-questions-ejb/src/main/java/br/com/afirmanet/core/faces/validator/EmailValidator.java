package br.com.afirmanet.core.faces.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import br.com.afirmanet.core.faces.Message;

@RequestScoped
@FacesValidator(EmailValidator.VALIDATOR_ID)
public class EmailValidator implements Validator {

	public static final String VALIDATOR_ID = "EmailValidator";
	public static final String INVALID_EMAIL = "br.com.afirmanet.faces.validator.EmailValidator.Email";
	public static final String REGEX = "[a-zA-Z0-9][\\w\\.\\-]*@[a-zA-Z0-9][\\w\\.\\-]*\\.[a-zA-Z][a-zA-Z\\.]*";

	@Inject
	private Message message;

	private Pattern pattern = Pattern.compile(REGEX);

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value == null) {
			return;
		}

		String email = (String) value;
		Matcher matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			throw new ValidatorException(message.getFacesErrorMessages(INVALID_EMAIL));
		}
	}
}
