package br.com.afirmanet.core.faces.converter;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import br.com.afirmanet.core.document.ie.Ie;
import br.com.afirmanet.core.document.ie.IeSaoPaulo;
import br.com.afirmanet.core.faces.Message;
import br.com.afirmanet.core.util.StringUtils;

@RequestScoped
@FacesConverter(IeConverter.CONVERTER_ID)
public class IeConverter implements Converter {

	public static final String CONVERTER_ID = "IeConverter";
	public static final String INVALID_IE = "br.com.afirmanet.faces.converter.IeConverter.IE";

	@Inject
	private Message message;

	private Ie ieSaoPaulo = new IeSaoPaulo();

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		try {
			if (ieSaoPaulo.isLegible(value)) {
				return StringUtils.removeNonDigits(value);
			}

			throw new ConverterException(message.getFacesErrorMessages(INVALID_IE));

		} catch (Exception e) {
			throw new ConverterException(message.getFacesErrorMessages(INVALID_IE));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || "".equals(value)) {
			return null;
		}

		String ie = (String) value;
		ie = StringUtils.removeNonDigits(ie);
		return ieSaoPaulo.format(ie);
	}

}
