package br.com.afirmanet.questions.constante;

import lombok.Getter;
import lombok.Setter;

public enum ExtensaoEnum {
	TXT("TXT", ".txt"), 
	CSV("CSV", ".csv"), 
	PDF("PDF", ".pdf");
	
	@Getter
	@Setter
	private String codigo;
	
	@Getter
	@Setter
	private String sufixo;
	
	private ExtensaoEnum(String codigo, String sufixo) {
		this.codigo = codigo;
		this.sufixo = sufixo;
	}
	
}
