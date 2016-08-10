package br.com.afirmanet.questions.grafico.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import br.com.afirmanet.questions.entity.Classificacao;
import lombok.Getter;
import lombok.Setter;

public class ClassificacaoChart {
	@Getter
	@Setter
	private LocalDateTime dataCadastro;
	
	@Getter
	@Setter
	private List<Classificacao> positivos = new ArrayList<>();
	
	@Getter
	@Setter
	private List<Classificacao> negativos = new ArrayList<>();
	
	@Getter
	@Setter
	private List<Classificacao> naoSeAplicam = new ArrayList<>();
	
	
	public String dataCadastroFormatada() {
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MM/yyyy");
		
		return this.dataCadastro.format(pattern);
	}
}
