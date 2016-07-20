package br.com.afirmanet.core.faces.converter;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.CepUtils;
import br.com.afirmanet.core.util.StringUtils;

@RequestScoped
@FacesConverter(CepConverter.CONVERTER_ID)
public class CepConverter implements Converter {

	public static final String CONVERTER_ID = "CepConverter";
	public static final String INVALID_CEP = "br.com.afirmanet.faces.converter.CepConverter.CEP";

	@Inject
	private Message message;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		try {
			if (CepUtils.isLegible(value)) {
				return StringUtils.removeNonDigits(value);
			}

			throw new ConverterException(message.getFacesErrorMessages(INVALID_CEP));

		} catch (Exception e) {
			throw new ConverterException(message.getFacesErrorMessages(INVALID_CEP));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || "".equals(value)) {
			return null;
		}

		String cep = (String) value;
		cep = StringUtils.removeNonDigits(cep);
		return CepUtils.format(cep);
	}

}