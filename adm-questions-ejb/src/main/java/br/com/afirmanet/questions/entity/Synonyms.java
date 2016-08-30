package br.com.afirmanet.questions.entity;

import java.io.Serializable;

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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@Table(name = "synonyms")
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "cliente" })
public class Synonyms implements Serializable {

	private static final long serialVersionUID = 7765713173667682152L;

	@Id
	@SequenceGenerator(name = "sq_synonyms", sequenceName = "sq_synonyms", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_synonyms")
	@Column(name = "id_synonyms")
	private Integer id;

	private String descricao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
}
