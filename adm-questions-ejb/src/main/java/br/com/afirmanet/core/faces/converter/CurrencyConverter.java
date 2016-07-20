package br.com.afirmanet.core.faces.converter;

import java.math.BigDecimal;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.BigDecimalUtils;

@RequestScoped
@FacesConverter(CurrencyConverter.CONVERTER_ID)
public class CurrencyConverter implements Converter {

	public static final String CONVERTER_ID = "CurrencyConverter";
	public static final String INVALID_CURRENCY = "br.com.afirmanet.faces.converter.CurrencyConverter.Currency";

	@Inject
	private Message message;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		try {
			BigDecimal decimal = BigDecimalUtils.parse(value);
			return decimal;

		} catch (Exception e) {
			throw new ConverterException(message.getFacesErrorMessages(INVALID_CURRENCY));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || "".equals(value)) {
			return null;
		}

		BigDecimal decimal = (BigDecimal) value;
		return BigDecimalUtils.format(decimal);
	}

}
