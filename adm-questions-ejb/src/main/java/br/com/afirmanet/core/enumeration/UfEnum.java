package br.com.afirmanet.core.enumeration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum UfEnum {

	AC(1),
	AL(2),
	AM(4),
	AP(3),
	BA(5),
	CE(6),
	DF(7),
	ES(8),
	GO(9),
	MA(10),
	MG(13),
	MS(12),
	MT(11),
	PA(14),
	PB(15),
	PE(17),
	PI(18),
	PR(16),
	RJ(19),
	RN(20),
	RO(22),
	RR(23),
	RS(21),
	SC(24),
	SE(26),
	SP(25),
	TO(27);

	private static final Map<String, UfEnum> LOOKUP = new HashMap<>();

	static {
		for (UfEnum e : EnumSet.allOf(UfEnum.class)) {
			LOOKUP.put(e.toString(), e);
		}
	}

	@Getter
	private Integer id;

	private UfEnum(Integer id) {
		this.id = id;
	}

	public String getSigla() {
		return toString();
	}

	public static UfEnum valueBySigla(String sigla) {
		return LOOKUP.get(sigla);
	}

}