package br.com.afirmanet.core.enumeration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum FileRepositoryActionEnum {

	LOAD_REPOSITORY("loadRepository"),
	SEARCH_FILES("searchFiles"),
	DOWNLOAD_FILE("downloadFile"),
	DELETE_FILE("deleteFile"),
	UPLOAD("upload");

	private static final Map<String, FileRepositoryActionEnum> lookup = new HashMap<>();

	static {

		for (FileRepositoryActionEnum e : EnumSet.allOf(FileRepositoryActionEnum.class)) {
			lookup.put(e.getCodigo(), e);
		}
	}

	@Getter
	private String codigo;

	private FileRepositoryActionEnum(String codigo) {
		this.codigo = codigo;
	}

	public static FileRepositoryActionEnum valueOfByCodigo(String codigo) {
		return lookup.get(codigo);
	}

}