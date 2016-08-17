package br.com.afirmanet.questions.constante;

import java.time.Month;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public enum MesEnum {
	
	JANEIRO(1, "Janeiro", Month.JANUARY), 
	FEVEREIRO(2, "Fevereiro", Month.FEBRUARY), 
	MARCO(3, "Março", Month.MARCH), 
	ABRIL(4, "Abril", Month.APRIL), 
	MAIO(5, "Maio", Month.MAY),
	JUNHO(6, "Junho", Month.JUNE), 
	JULHO(7, "Julho", Month.JULY), 
	AGOSTO(8, "Agosto", Month.AUGUST), 
	SETEMBRO(9, "Setembro", Month.SEPTEMBER), 
	OUTUBRO(10, "Outubro", Month.OCTOBER), 
	NOVEMBRO(11, "Novembro", Month.NOVEMBER), 
	DEZEMBRO(12, "Dezembro", Month.DECEMBER);
	
	@Getter
	@Setter
	private Integer codigo;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private Month month;
	
	private MesEnum(Integer codigo, String descricao, Month month) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.month = month;
	}
	
	public static List<MesEnum> getMeses() {
		return Arrays.asList(MesEnum.values());
	}
}
