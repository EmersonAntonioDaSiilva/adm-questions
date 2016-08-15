package br.com.afirmanet.questions.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@Table(name = "classificacoes")
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "cliente", "topico" })
public class Classificacao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sq_classificacoes", sequenceName = "sq_classificacoes", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_classificacoes")
	@Column(name = "id_classificacao")
	private Integer id;

	private String pergunta;
	private String resposta;
	private Double confidence;
	
	private Integer sentimento;
	
	private LocalDateTime dataCadastro;
	private LocalDateTime dataClassificacao;

	private String classifier;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_topico")
	private Topico topico;	
	
	@Transient
	public String getDataCadastroFormatMesAno(){
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MM/yyyy");
		
		return dataCadastro.format(pattern);
	}
	
	@Transient
	public Integer getRegistro(){
		return 1;
	}
}
