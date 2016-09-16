package br.com.afirmanet.questions.constante;

import lombok.Getter;
import lombok.Setter;

public enum RelatorioClassificacaoEnum {
	
	SENTIMENTO_POSITIVO(1, "Positivo", "Positivos", "009900"),
	SENTIMENTO_IMPARCIAL(0, "Imparcial", "Imparciais", "CC0000"),
	SENTIMENTO_NEGATIVO(-1, "Negativo", "Negativos", "D1E0E0");
	
	@Getter
	@Setter
	private Integer codigo;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private String chartLabel;
	
	@Getter
	@Setter
	private String corHexadecimal;
	
	private RelatorioClassificacaoEnum(Integer codigo, String descricao, String chartLabel, String corHexadecimal) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.chartLabel = chartLabel;
		this.corHexadecimal = corHexadecimal;
	}

}
