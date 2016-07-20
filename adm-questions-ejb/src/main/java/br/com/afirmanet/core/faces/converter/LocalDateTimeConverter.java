package br.com.afirmanet.core.faces.converter;

import java.time.LocalDateTime;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.DateUtils;

@RequestScoped
@FacesConverter(LocalDateTimeConverter.CONVERTER_ID)
public class LocalDateTimeConverter implements Converter {

	public static final String CONVERTER_ID = "LocalDateTimeConverter";
	public static final String INVALID_DATE = "br.com.afirmanet.faces.converter.DateConverter.Date";

	@Inject
	private Message message;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		try {
			LocalDateTime date = DateUtils.parseLocalDateTime(value);
			return date;

		} catch (Exception e) {
			throw new ConverterException(message.getFacesErrorMessages(INVALID_DATE));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || "".equals(value)) {
			return null;
		}

		LocalDateTime date = (LocalDateTime) value;
		return DateUtils.format(date);
	}

}
