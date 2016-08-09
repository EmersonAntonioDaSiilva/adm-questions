package br.com.afirmanet.questions.constante;

import lombok.Getter;
import lombok.Setter;

public enum RelatorioClassificacaoEnum {
	
	SENTIMENTO_POSITIVO(1, "Positivo", "Positivos"),
	SENTIMENTO_IMPARCIAL(0, "Imparcial", "Imparciais"),
	SENTIMENTO_NEGATIVO(-1, "Negativo", "Negativos");
	
	@Getter
	@Setter
	private Integer codigo;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private String chartLabel;
	
	private RelatorioClassificacaoEnum(Integer codigo, String descricao, String chartLabel) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.chartLabel = chartLabel;
	}

}
