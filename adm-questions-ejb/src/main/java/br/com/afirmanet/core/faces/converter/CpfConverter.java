package br.com.afirmanet.core.faces.converter;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.CpfUtils;
import br.com.afirmanet.core.util.StringUtils;

@RequestScoped
@FacesConverter(CpfConverter.CONVERTER_ID)
public class CpfConverter implements Converter {

	public static final String CONVERTER_ID = "CpfConverter";
	public static final String INVALID_CPF = "br.com.afirmanet.faces.converter.CpfConverter.CPF";

	@Inject
	private Message message;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		try {
			if (CpfUtils.isLegible(value)) {
				return StringUtils.removeNonDigits(value);
			}

			throw new ConverterException(message.getFacesErrorMessages(INVALID_CPF));

		} catch (Exception e) {
			throw new ConverterException(message.getFacesErrorMessages(INVALID_CPF));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || "".equals(value)) {
			return null;
		}

		String cpf = (String) value;
		cpf = StringUtils.removeNonDigits(cpf);
		return CpfUtils.format(cpf);
	}

}
