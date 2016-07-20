package br.com.afirmanet.questions.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

import lombok.Data;

@SessionScoped
@Data
public class Identidade implements Serializable {

	private static final long serialVersionUID = 3662438562276471329L;

	private String ipRequisicao;
	private boolean logadoPelaIntranet;
	private boolean loginConfirmado;

}
