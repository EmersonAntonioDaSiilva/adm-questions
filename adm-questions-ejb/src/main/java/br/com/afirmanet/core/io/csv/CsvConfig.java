package br.com.afirmanet.core.io.csv;

import java.nio.charset.Charset;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import br.com.afirmanet.core.util.IoUtils;

/**
 * <p>
 * Representa as configurações utilizadas no processamento de leitura e escrita de arquivos no formato .csv.
 * </p>
 */
@Slf4j
@Data
public class CsvConfig {

	/**
	 * <p>
	 * Delimitador padrão de campos. O valor default é <i>;</i> (vírgula).
	 * </p>
	 */
	public static final Character DEFAULT_DELIMITER = ';';

	/**
	 * <p>
	 * Delimitador padrão de texto. O valor padrão é <i>"</i>
	 * </p>
	 */
	public static final Character DEFAULT_QUOTE = '"';

	/**
	 * <p>
	 * Caracter padrão que indica início de comentário. O valor padrão é <i>#</i>
	 * </p>
	 */
	public static final Character DEFAULT_START_COMMENT = '#';

	private Character delimiter;
	private Character quote;
	private Character commnetStart;
	private Charset charset;
	private String header;

	/**
	 * <p>
	 * Realiza o parser do objeto que detém as configurações utilizadas no processamento de leitura e escrita de
	 * arquivos no formato .csv, eliminando eventuais problemas que possa ocorrer durante o processamento de um dado
	 * arquivo, garantindo a integridade das configurações passadas.
	 * </p>
	 * 
	 * @param delimiter
	 *        um <code>Character</code> que representa o delimitador de campos
	 * @param quote
	 *        um <code>Character</code> que representa o delimitador de textos
	 * @param charset
	 *        um <code>Charset</code> que representa o encoding do arquivo
	 */
	public CsvConfig(Character delimiter, Character quote, Charset charset) {
		this.delimiter = checkDelimiter(delimiter);
		this.quote = checkQuote(quote);
		this.charset = checkCharset(charset);
	}

	public CsvConfig() {
		delimiter = DEFAULT_DELIMITER;
		quote = DEFAULT_QUOTE;
		charset = IoUtils.DEFAULT_CHARSET;
	}

	/**
	 * <p>
	 * Verifica se o caracter utilizado para delimitação de campo passado por parâmetro é válido, caso não seja, será
	 * retornado o caracter de delimitação padrão <i>;</i>.
	 * </p>
	 * 
	 * @param delimiter
	 *        um <code>Character</code> que representa o delimitador de campos
	 * 
	 * @return delimitador de campos
	 */
	private static Character checkDelimiter(Character delimiter) {
		if (delimiter == null) {
			log.debug("Nenhum delimitador de campo foi informado. Utilizando o padrão [" + CsvConfig.DEFAULT_DELIMITER + "]!");

			return CsvConfig.DEFAULT_DELIMITER;
		}

		return delimiter;
	}

	/**
	 * <p>
	 * Verifica se o caracter utilizado para delimitação de texto passado por parâmetro é válido, caso não seja, será
	 * retornado o caracter de delimitação padrão <i>"</i>.
	 * </p>
	 * 
	 * @param quote
	 *        um <code>Character</code> que representa o delimitador de textos
	 * 
	 * @return delimitador de textos
	 */
	private static Character checkQuote(Character quote) {
		if (quote == null) {
			log.debug("Nenhum caracter de delimitação de texto foi informado. Utilizando o padrão [" + CsvConfig.DEFAULT_QUOTE + "]!");

			return CsvConfig.DEFAULT_QUOTE;
		}

		return quote;
	}

	/**
	 * <p>
	 * Verifica se o enconding passado por parâmetro é válido, caso não seja, será retornado o enconding padrão
	 * <i>UTF-8</i>.
	 * </p>
	 * 
	 * @param charset
	 *        um <code>Charset</code> que representa o enconding utilizado no processamento do arquivo
	 * 
	 * @return enconding utilizado no processamento do arquivo
	 */
	private static Charset checkCharset(Charset charset) {
		if (charset == null) {
			log.debug("Nenhum CHARSET foi informado. Utilizando o padrão [" + IoUtils.DEFAULT_CHARSET.displayName() + "]!");

			return IoUtils.DEFAULT_CHARSET;
		}

		return charset;
	}

	/**
	 * <p>
	 * Verifica se o arquivo para processamento possui cabeçalho.
	 * </p>
	 * 
	 * @return um <code>boolean</code> indicando true para a existência de cabeçalho, false caso contrário
	 */
	public boolean hasHeader() {
		if (StringUtils.isEmpty(header)) {
			return false;
		}
		return true;
	}

}
