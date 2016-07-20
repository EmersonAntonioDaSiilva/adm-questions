package br.com.afirmanet.core.faces.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import br.com.afirmanet.core.enumeration.TipoPessoaEnum;
import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.CpfCnpjUtils;
import br.com.afirmanet.core.util.StringUtils;

@FacesConverter(CpfCnpjConverter.CONVERTER_ID)
public class CpfCnpjConverter implements Converter {

	public static final String CONVERTER_ID = "CpfCnpjConverter";
	public static final String INVALID_CPF_CNPJ = "br.com.afirmanet.faces.converter.CpfCnpjConverter.CPF_CNPJ";

	@Inject
	private Message message;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		Object returnValue = null;

		TipoPessoaEnum tipoPessoaEnum = CpfCnpjUtils.getPersonType(value);
		if (TipoPessoaEnum.FISICA == tipoPessoaEnum) {
			returnValue = new CpfConverter().getAsObject(context, component, value);
		} else if (TipoPessoaEnum.JURIDICA == tipoPessoaEnum) {
			returnValue = new CnpjConverter().getAsObject(context, component, value);
		} else {
			throw new ConverterException(message.getFacesErrorMessages(INVALID_CPF_CNPJ));
		}

		return returnValue;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || "".equals(value)) {
			return null;
		}

		String cpfOrCnpj = (String) value;
		TipoPessoaEnum tipoPessoaEnum = CpfCnpjUtils.getPersonType(cpfOrCnpj);
		if (TipoPessoaEnum.FISICA == tipoPessoaEnum) {
			cpfOrCnpj = new CpfConverter().getAsString(context, component, value);
		} else if (TipoPessoaEnum.JURIDICA == tipoPessoaEnum) {
			cpfOrCnpj = new CnpjConverter().getAsString(context, component, value);
		}

		return cpfOrCnpj;
	}

}
