package br.com.afirmanet.core.util;

import br.com.afirmanet.core.document.ie.IeSaoPaulo;

/**
 * <p>
 * Classe utilitária para manipulação de Ie (Inscrição Estadual).
 * </p>
 * 
 * <ul>
 * <li><b>format</b> - formata o Ie de acordo com o formato exibido em sua UF</li>
 * </ul>
 * 
 */
public final class IeUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private IeUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	/**
	 * <p>
	 * Formata a Ie informada utilizando a <b>formatação default (Estado de São Paulo)</b>.
	 * </p>
	 * 
	 * <pre>
	 * IeUtils.format("123456789012")    = 123.456.789.012
	 * IeUtils.format("123.456.789.012") = 123.456.789.012
	 * </pre>
	 * 
	 * @param ie
	 *        Ie a ser formatada
	 * 
	 * @return Ie formatada
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>ie</code> estiver <code>null</code>, vazio <code>("")</code>, em branco
	 *         <code>(" ")</code> ou não estiver legível
	 * 
	 */
	public static String format(String ie) {
		IeSaoPaulo ieSaoPaulo = new IeSaoPaulo();
		return ieSaoPaulo.format(ie);
	}

	/**
	 * <p>
	 * Verifica se a Ie informada é legível utilizando as regras estabelecidas para o <b>Estado default (São Paulo)</b>,
	 * ou seja, possui um formato passível de leitura.
	 * </p>
	 * 
	 * <pre>
	 * IeUtils.isLegible("123456789012")        = true
	 * IeUtils.isLegible("123.456.789.012")     = true
	 * IeUtils.isLegible("000000000000")        = true
	 * IeUtils.isLegible("123.456.789.012.345") = false
	 * IeUtils.isLegible("123456")              = false
	 * IeUtils.isLegible("asdds1235")           = false
	 * IeUtils.isLegible("  123456")            = false
	 * </pre>
	 * 
	 * @param ie
	 *        Ie a ser avaliada
	 * 
	 * @return <code>true</code> se a Ie for legível, caso contrário <code>false</code>
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>ie</code> estiver <code>null</code>, vazio <code>("")</code> ou com espaço em branco
	 *         <code>(" ")</code>
	 * 
	 */
	public static boolean isLegible(final String ie) {
		IeSaoPaulo ieSaoPaulo = new IeSaoPaulo();
		return ieSaoPaulo.isLegible(ie);
	}

}
