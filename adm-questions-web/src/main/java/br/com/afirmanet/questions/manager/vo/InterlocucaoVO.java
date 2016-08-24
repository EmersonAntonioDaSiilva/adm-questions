package br.com.afirmanet.questions.manager.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class InterlocucaoVO implements Serializable{
	private static final long serialVersionUID = 6592105917263633837L;

	private String idConversation;
	private String idCliente;
	private String horario;
	private String nome;
	private String interlocutor;

}
