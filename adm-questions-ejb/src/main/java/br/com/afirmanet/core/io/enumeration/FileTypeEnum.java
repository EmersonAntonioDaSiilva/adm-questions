package br.com.afirmanet.core.io.enumeration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * <p>
 * Enumera tipos de arquivos.
 * </p>
 * 
 */
public enum FileTypeEnum {

	CSV(".csv"),
	DOC(".doc"),
	PDF(".pdf"),
	TXT(".txt"),
	XLS(".xls"),
	XML(".xml"),
	XSD(".xsd"),
	ZIP(".zip");

	private static final Map<String, FileTypeEnum> lookup = new HashMap<>();

	static {
		for (FileTypeEnum e : EnumSet.allOf(FileTypeEnum.class)) {
			lookup.put(e.getExtension(), e);
		}
	}

	@Getter
	private String extension;

	private FileTypeEnum(String type) {
		extension = type;
	}

	public static FileTypeEnum valueOfByFileExtension(String fileExtension) {
		return lookup.get(fileExtension);
	}

	@Override
	public String toString() {
		return extension;
	}

}