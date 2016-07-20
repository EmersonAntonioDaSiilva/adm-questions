package br.com.afirmanet.core.enumeration;

/**
 * <p>
 * Enumera todos os tipos de conteúdo disponíveis para download.
 * </p>
 * 
 */
public enum ContentTypeEnum {

	PDF("application/pdf"),
	XLS("application/vnd.ms-excel"),
	DOC("application/msword"),
	CSV("application/csv"),
	TXT("application/txt"),
	ZIP("application/zip"),
	XML("application/xml"),
	XSD("application/xsd");

	private String type;

	private ContentTypeEnum(String type) {
		this.type = type;
	}

	public String getValue() {
		return type;
	}
}
