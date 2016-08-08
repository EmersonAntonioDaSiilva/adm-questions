package br.com.afirmanet.questions.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.afirmanet.questions.enums.TypeServicoEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "dados_watson")
@EqualsAndHashCode(of = "id")
public class DadosWatson implements Serializable {
	private static final long serialVersionUID = 7669388365460088024L;

	@Id
	@SequenceGenerator(name = "sq_dados_watson", sequenceName = "sq_dados_watson", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_dados_watson")
	@Column(name = "id_watson")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_topico")
	private Topico topico;

	@Enumerated(EnumType.STRING)
	@Column(name = "typo_servico")
	private TypeServicoEnum typeServico;
	  
	private String usuario;
	private String senha;
	private LocalDate dataCadastro;
}
