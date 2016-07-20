package br.com.afirmanet.core.faces.converter;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.CnpjUtils;
import br.com.afirmanet.core.util.StringUtils;

@RequestScoped
@FacesConverter(CnpjConverter.CONVERTER_ID)
public class CnpjConverter implements Converter {

	public static final String CONVERTER_ID = "CnpjConverter";
	public static final String INVALID_CNPJ = "br.com.afirmanet.faces.converter.CnpjConverter.CNPJ";

	@Inject
	private Message message;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		try {
			if (CnpjUtils.isLegible(value)) {
				return StringUtils.removeNonDigits(value);
			}

			throw new ConverterException(message.getFacesErrorMessages(INVALID_CNPJ));

		} catch (Exception e) {
			throw new ConverterException(message.getFacesErrorMessages(INVALID_CNPJ));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || "".equals(value)) {
			return null;
		}

		String cnpj = (String) value;
		cnpj = StringUtils.removeNonDigits(cnpj);
		return CnpjUtils.format(cnpj);
	}

}
