package br.com.afirmanet.core.document.factory;

import br.com.afirmanet.core.document.exception.IeException;
import br.com.afirmanet.core.document.ie.Ie;
import br.com.afirmanet.core.document.ie.IeSaoPaulo;
import br.com.afirmanet.core.enumeration.UfEnum;

public final class IeFactory {

	public static Ie getInscricaoEstadual(final UfEnum ufEnum) {
		Ie ie = null;

		if (UfEnum.SP == ufEnum) {
			ie = new IeSaoPaulo();
		} else {
			throw new IeException("Não existe implementação para a UF {0}!", ufEnum);
		}

		return ie;
	}
}
