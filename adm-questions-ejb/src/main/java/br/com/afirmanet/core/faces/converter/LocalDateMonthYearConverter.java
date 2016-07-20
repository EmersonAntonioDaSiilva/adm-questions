package br.com.afirmanet.core.faces.converter;

import java.time.LocalDate;

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
@FacesConverter(LocalDateMonthYearConverter.CONVERTER_ID)
public class LocalDateMonthYearConverter implements Converter {

	public static final String CONVERTER_ID = "LocalDateMonthYearConverter";
	public static final String INVALID_DATE = "br.com.afirmanet.faces.converter.DateConverter.Date";

	@Inject
	private Message message;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		try {
			value = "01/" + value;
			LocalDate date = DateUtils.parseLocalDate(value, "dd/MM/yyyy");
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

		LocalDate date = (LocalDate) value;
		return DateUtils.format(date, "MM/yyyy");
	}

}
