package br.com.afirmanet.core.enumeration;

import lombok.Getter;

/**
 * <p>
 * Enumera todos os tipos de pessoa.
 * </p>
 * 
 */
public enum TipoPessoaEnum {

	FISICA("F"),
	JURIDICA("J");

	@Getter
	private String sigla;

	TipoPessoaEnum(String sigla) {
		this.sigla = sigla;
	}

}
