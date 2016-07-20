package br.com.afirmanet.questions.modelo;

import lombok.Getter;
import lombok.Setter;

public class Questao {
	
	@Getter
	@Setter
	private String pergunta;
	
	@Getter
	@Setter
	private String resposta;
	
	public Questao() {}
	
	public Questao(String pergunta, String resposta) {
		this.pergunta = pergunta;
		this.resposta = resposta;
	}
}
