package br.com.afirmanet.questions.constante;

import lombok.Getter;
import lombok.Setter;

public enum SolrConfigEnum {
	
	PROTOWORDS("PROTOWORDS", "ProtoWords", "protowords"), 
	SYNONYMS("SYNONYMS", "Synonyms", "synonyms"),
	STOPWORDS("STOPWORDS", "Stopwords", "stopwords");
	
	@Getter
	@Setter
	private String codigo;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private String nomeArquivo;

	private SolrConfigEnum(String codigo, String descricao, String nomeArquivo) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.nomeArquivo = nomeArquivo;
	}
}
