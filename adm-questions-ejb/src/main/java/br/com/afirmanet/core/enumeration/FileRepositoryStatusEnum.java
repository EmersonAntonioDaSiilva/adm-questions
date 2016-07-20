package br.com.afirmanet.core.enumeration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum FileRepositoryStatusEnum {

	OK(1),
	FILE_NOT_FOUND(2),
	FILE_SIZE_EXCEEDED(3);

	private static final Map<Integer, FileRepositoryStatusEnum> lookup = new HashMap<>();

	static {

		for (FileRepositoryStatusEnum e : EnumSet.allOf(FileRepositoryStatusEnum.class)) {
			lookup.put(e.getCodigo(), e);
		}
	}

	@Getter
	private Integer codigo;

	private FileRepositoryStatusEnum(Integer codigo) {
		this.codigo = codigo;
	}

	public static FileRepositoryStatusEnum valueOfByCodigo(Integer codigo) {
		return lookup.get(codigo);
	}

}