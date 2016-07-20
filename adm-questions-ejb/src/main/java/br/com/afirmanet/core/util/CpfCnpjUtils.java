package br.com.afirmanet.core.util;

import br.com.afirmanet.core.enumeration.TipoPessoaEnum;

/**
 * <p>
 * Classe utilitária para manipulação de CPF (Cadastro de Pessoa Física) e CNPJ (Cadastro Nacional de Pessoa Jurídica).
 * </p>
 * 
 * <ul>
 * <li><b>format</b> - formata o documento, deixando-o com a seguinte máscara: 99.999.999/9999-99 para CNPJ ou
 * 999.999.999-99 para CPF</li>
 * <li><b>isValid</b> - verifica se o documento é válido</li>
 * <li><b>getPersonType</b> - obtém o tipo de pessoa (FISICA ou JURIDICA) vinculado ao documento</li>
 * </ul>
 * 
 * @see CpfUtils
 * @see CnpjUtils
 * 
 */
public final class CpfCnpjUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private CpfCnpjUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	/**
	 * <p>
	 * Formata o documento (CPF ou CNPJ) informado.
	 * </p>
	 * 
	 * <pre>
	 * CpfCnpjUtils.format("95434756525")        = 954.347.565-25
	 * CpfCnpjUtils.format("954.347.565-25")     = 954.347.565-25
	 * CpfCnpjUtils.format("00000000000191")     = 00.000.000/0001-91
	 * CpfCnpjUtils.format("00.000.000/0001-91") = 00.000.000/0001-91
	 * </pre>
	 * 
	 * @param cpfOrCnpj
	 *        documento (CPF ou CNPJ) a ser formatado
	 * 
	 * @return documento (CPF ou CNPJ) formatado
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cpfOrCnpj</code> estiver <code>null</code>, vazio <code>("")</code>, espaço em
	 *         branco <code>(" ")</code>, não estiver legível ou não for um CPF e nem um CNPJ
	 * 
	 */
	public static String format(final String cpfOrCnpj) {
		TipoPessoaEnum personType = getPersonType(cpfOrCnpj);

		if (TipoPessoaEnum.FISICA == personType) {
			return CpfUtils.format(cpfOrCnpj);
		}

		if (TipoPessoaEnum.JURIDICA == personType) {
			return CnpjUtils.format(cpfOrCnpj);
		}

		throw new IllegalArgumentException("Parâmetro informado não corresponde a um CPF e nem a um CNPJ.");
	}

	/**
	 * <p>
	 * Verifica se o documento (CPF ou CNPJ) informado é válido.
	 * </p>
	 * 
	 * <pre>
	 * CpfCnpjUtils.isValid("00.000.000/0001-91") = true
	 * CpfCnpjUtils.isValid("00000000000191")     = true
	 * CpfCnpjUtils.isValid("40.695.212/0001-12") = false // Dígito correto é 55
	 * CpfCnpjUtils.isValid("00000000000000")     = false
	 * CpfCnpjUtils.isValid("954.347.565-25")     = true
	 * CpfCnpjUtils.isValid("95434756525")        = true
	 * CpfCnpjUtils.isValid("954.347.565-10")     = false // Dígito correto é 25
	 * CpfCnpjUtils.isValid("00000000000")        = false
	 * CpfCnpjUtils.isValid("123456")             = false
	 * CpfCnpjUtils.isValid("abc")                = false
	 * </pre>
	 * 
	 * <p>
	 * <b>NOTA:</b> Caso o documento informado seja <code>null</code>, vazio <code>("")</code> ou espaço em branco
	 * <code>(" ")</code>, será retornado <code>false</code>.
	 * <p>
	 * 
	 * @param cpfOrCnpj
	 *        documento a ser avaliado
	 * 
	 * @return <code>true</code> se o documento (CPF ou CNPJ) for válido, caso contrário <code>false</code>
	 * 
	 */
	public static boolean isValid(final String cpfOrCnpj) {
		TipoPessoaEnum personType = getPersonType(cpfOrCnpj);

		boolean isValid = false;
		if (TipoPessoaEnum.FISICA == personType) {
			isValid = CpfUtils.isValid(cpfOrCnpj);
		}

		if (TipoPessoaEnum.JURIDICA == personType) {
			isValid = CnpjUtils.isValid(cpfOrCnpj);
		}

		return isValid;
	}

	/**
	 * <p>
	 * Obtém o tipo de pessoa (FÍSICA ou JURÍDICA) a qual o documento informado pertence.
	 * </p>
	 * 
	 * <pre>
	 * CpfCnpjUtils.getPersonType("00000000000191")     = PersonType.JURIDICA
	 * CpfCnpjUtils.getPersonType("00.000.000/0001-91") = PersonType.JURIDICA
	 * CpfCnpjUtils.getPersonType("95434756525")        = PersonType.FISICA
	 * CpfCnpjUtils.getPersonType("954.347.565-25")     = PersonType.FISICA
	 * </pre>
	 * 
	 * @param cpfOrCnpj
	 *        documento a ser avaliado
	 * 
	 * @return tipo de pessoa vinculada ao documento informado
	 * 
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>cpfOrCnpj</code> estiver <code>null</code>, vazio <code>("")</code>, espaço em
	 *         branco <code>(" ")</code>, não estiver legível ou não for um CPF e nem um CNPJ
	 * 
	 */
	public static TipoPessoaEnum getPersonType(final String cpfOrCnpj) {
		TipoPessoaEnum personType = null;

		if (CpfUtils.isLegible(cpfOrCnpj)) {
			personType = TipoPessoaEnum.FISICA;
		} else if (CnpjUtils.isLegible(cpfOrCnpj)) {
			personType = TipoPessoaEnum.JURIDICA;
		}

		return personType;
	}

}
