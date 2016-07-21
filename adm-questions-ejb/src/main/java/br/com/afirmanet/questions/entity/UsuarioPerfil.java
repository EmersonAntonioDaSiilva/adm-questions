package br.com.afirmanet.questions.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "usuario_perfil")
@EqualsAndHashCode(of = "id")
public class UsuarioPerfil implements Serializable {
	private static final long serialVersionUID = -3470820107606783069L;

	@Id
	@SequenceGenerator(name = "sq_usuario_perfil", sequenceName = "sq_usuario_perfil", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_usuario_perfil")
	@Column(name = "id_usuario_perfil")
	private Integer id;
	
	
	private String clientId;
	private String conversationId;
	private String dialogId;
	private String nome;
	private String email;
	private LocalDate dataNascimento;
	private LocalDate dataAdmissao;
}
