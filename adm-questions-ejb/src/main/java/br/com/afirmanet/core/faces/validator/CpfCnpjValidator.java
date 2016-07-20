package br.com.afirmanet.core.faces.validator;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import br.com.afirmanet.core.enumeration.TipoPessoaEnum;
import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.CpfCnpjUtils;

@RequestScoped
@FacesValidator(CpfCnpjValidator.VALIDATOR_ID)
public class CpfCnpjValidator implements Validator {

	public static final String VALIDATOR_ID = "CpfCnpjValidator";
	public static final String INVALID_CPF_CNPJ = "br.com.afirmanet.faces.validator.CpfCnpjValidator.CPF_CNPJ";

	@Inject
	private Message message;

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value == null) {
			return;
		}

		String cpfOrCnpj = (String) value;
		TipoPessoaEnum tipoPessoaEnum = CpfCnpjUtils.getPersonType(cpfOrCnpj);
		if (TipoPessoaEnum.FISICA == tipoPessoaEnum) {
			new CpfValidator().validate(facesContext, uiComponent, value);

		} else if (TipoPessoaEnum.JURIDICA == tipoPessoaEnum) {
			new CnpjValidator().validate(facesContext, uiComponent, value);

		} else {
			throw new ValidatorException(message.getFacesErrorMessages(INVALID_CPF_CNPJ));
		}
	}

}
