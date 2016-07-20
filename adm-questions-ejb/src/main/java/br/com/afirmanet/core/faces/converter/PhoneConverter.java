package br.com.afirmanet.core.faces.converter;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.PhoneUtils;
import br.com.afirmanet.core.util.StringUtils;

@RequestScoped
@FacesConverter(PhoneConverter.CONVERTER_ID)
public class PhoneConverter implements Converter {

	public static final String CONVERTER_ID = "PhoneConverter";
	public static final String INVALID_PHONE = "br.com.afirmanet.faces.converter.PhoneConverter.PHONE";

	@Inject
	private Message message;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		try {
			Long phone = PhoneUtils.parse(value);
			if (phone == null) {
				throw new ConverterException(message.getFacesErrorMessages(INVALID_PHONE));
			}

			return phone;

		} catch (Throwable e) {
			throw new ConverterException(message.getFacesErrorMessages(INVALID_PHONE));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}

		try {
			String phone = PhoneUtils.format(String.valueOf(value), true);
			return phone;

		} catch (Exception e) {
			throw new ConverterException(message.getFacesErrorMessages(INVALID_PHONE));
		}
	}

}
