package br.com.afirmanet.questions.manager.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class DialogVO implements Serializable {
	private static final long serialVersionUID = -6114713261531714391L;

	private String idDialog;
	private String idConversation;
	private String idCliente;
	private String horario;
	private String pessoa;
	private String dialogo;
}
