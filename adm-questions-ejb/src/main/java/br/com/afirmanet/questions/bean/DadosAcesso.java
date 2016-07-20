package br.com.afirmanet.questions.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

import lombok.Data;

@SessionScoped
@Data
public class DadosAcesso implements Serializable {
	private static final long serialVersionUID = -2258342378534533796L;

	private String  idOrigem;
	private String  soOrigem;
	private String  browserOrigem;
	private String  versaoAplicacaoOrigem;

}
