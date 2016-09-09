package br.com.afirmanet.questions.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@Table(name = "solr_cluster")
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "cliente" })
public class ClusterSolr implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Status {
		ATIVO, INATIVO;
	}
	
	public enum Unidade {
		ZERO(0, null, "FREE", ""),
		UMA(1, 1, "32GB", "4GB"),
		DUAS(2, 2, "8GB", ""),
		TRES(3, 3, "12GB", ""), 
		QUATRO(4, 4, "16GB", ""),
		CINCO(5, 5, "20GB", ""), 
		SEIS(6, 6, "24GB", ""),
		SETE(7, 7, "224GB", "");
		
		@Getter
		@Setter
		private Integer quantidade;
		
		@Getter
		@Setter
		private Integer valor;
		
		@Getter
		@Setter
		private String capacidadeEmDisco;
		
		@Getter
		@Setter
		private String capacidadedeMemoriaRAM;
		
		private Unidade(Integer quantidade, Integer valor, String capacidadeEmDisco, String capacidadedeMemoriaRAM) {
			this.quantidade = quantidade;
			this.valor = valor;
			this.capacidadeEmDisco = capacidadeEmDisco;
			this.capacidadedeMemoriaRAM = capacidadedeMemoriaRAM;
		}
	}
	
	@Id
	@SequenceGenerator(name = "sq_solr_cluster", sequenceName = "sq_solr_cluster", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_solr_cluster")
	@Column(name = "id_solr_cluster")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@Column(name = "nome_cluster")
	private String nomeCluster;
	
	@Column(name = "id_cluster")
	private String idCluster;
	
	@Column(name = "unit_cluster")
	private Integer unitCluster;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status_cluster")
	private Status statusCluster;
	
	private LocalDateTime datacadastro;
	
	@Column(name = "nome_config")
	private String nomeConfig;
	
	@Column(name = "nome_collection")
	private String nomeCollection;
}
