package br.com.afirmanet.core.util;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.ResultSet;

import br.com.afirmanet.core.io.csv.CsvConfig;
import br.com.afirmanet.core.io.csv.CsvWriter;

/**
 * <p>
 * Classe utilitária para manipulação de arquivos com formato <i>.csv</i>.
 * </p>
 * 
 * <ul>
 * <li><b>writer</b> - escreve o conteúdo de um <code>String[]</code> ou de um <code>ResultSet</code> no arquivo
 * informado</li>
 * </ul>
 * 
 * @see CsvWriter
 * @see CsvConfig
 * 
 */
public final class CsvUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private CsvUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	/**
	 * <p>
	 * Escreve o conteúdo do <code>ResultSet</code> no arquivo informado, aplicando as configurações passadas.
	 * </p>
	 * 
	 * <pre>
	 * CsvUtils.writer(';', '&quot;', Charset.forName(&quot;UTF-8&quot;), new File(&quot;C:\\teste.csv&quot;), new ResultSetImpl(), &quot;dd/MM/yyyy HH:mm:ss&quot;, &quot;#,##0.00&quot;)
	 * </pre>
	 * 
	 * @param delimiter
	 *        um <code>Character</code> que representa o delimitador de campos dos registros
	 * @param quote
	 *        um <code>Character</code> que representa o delimitador de dados para cada campo
	 * @param charset
	 *        um <code>Charset</code> que representa o encoding utilizado no arquivo
	 * @param outputFile
	 *        um <code>File</code> que receberá o conteúdo do <code>ResultSet</code>, lembrando que o mesmo deve possuir
	 *        o caminho completo do arquivo e não somente o nome do arquivo
	 * @param resultSet
	 *        um <code>ResultSet</code> que detém o conteúdo a ser escrito no arquivo
	 * @param datePattern
	 *        uma <code>String</code> que representa o formato a ser aplicado nas colunas do tipo Data
	 * @param numberPattern
	 *        uma <code>String</code> que representa o formato a ser aplicado nas colunas do tipo Decimal
	 */
	public static void writer(final Character delimiter, final Character quote, final Charset charset, final File outputFile, final ResultSet resultSet, final String datePattern,
			final String numberPattern) {
		CsvWriter csvWriter = new CsvWriter(new CsvConfig(delimiter, quote, charset));
		csvWriter.writer(outputFile, resultSet, datePattern, numberPattern);
	}

	/**
	 * <p>
	 * Escreve o conteúdo do <code>ResultSet</code> no arquivo informado, aplicando as configurações passadas.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTA:</b> As colunas do tipo data receberão o formato #def
	 * {@link br.com.afirmanet.core.util.DateUtils#DEFAULT_TIMESTAMP_PATTERN} e as colunas do tipo decimal receberão o
	 * formato {@link br.com.afirmanet.core.util.BigDecimalUtils#DEFAULT_DECIMAL_PATTERN}.
	 * </p>
	 * 
	 * <pre>
	 * CsvUtils.writer(';', '&quot;', Charset.forName(&quot;UTF-8&quot;), new File(&quot;C:\\teste.csv&quot;), new ResultSetImpl())
	 * </pre>
	 * 
	 * @param delimiter
	 *        um <code>Character</code> que representa o delimitador de campos dos registros
	 * @param quote
	 *        um <code>Character</code> que representa o delimitador de dados para cada campo
	 * @param charset
	 *        um <code>Charset</code> que representa o encoding utilizado no arquivo
	 * @param outputFile
	 *        um <code>File</code> que receberá o conteúdo do resultSet, lembrando que o mesmo deve possuir o caminho
	 *        completo do arquivo e não somente o nome do arquivo
	 * @param resultSet
	 *        um <code>ResultSet</code> que detém o conteúdo a ser escrito no arquivo
	 * 
	 */
	public static void writer(final Character delimiter, final Character quote, final Charset charset, final File outputFile, final ResultSet resultSet) {
		CsvWriter csvWriter = new CsvWriter(new CsvConfig(delimiter, quote, charset));
		csvWriter.writer(outputFile, resultSet, DateUtils.DEFAULT_TIMESTAMP_PATTERN, BigDecimalUtils.DEFAULT_DECIMAL_PATTERN);
	}

	public static void writer(final File outputFile, final ResultSet resultSet, final Boolean append, Boolean format) {
		CsvWriter csvWriter = new CsvWriter(new CsvConfig(CsvConfig.DEFAULT_DELIMITER, CsvConfig.DEFAULT_QUOTE, IoUtils.DEFAULT_CHARSET));
		csvWriter.writer(outputFile, resultSet, DateUtils.DEFAULT_TIMESTAMP_PATTERN, BigDecimalUtils.DEFAULT_DECIMAL_PATTERN, append, format);
	}

	/**
	 * <p>
	 * Escreve o conteúdo do <code>ResultSet</code> no diretório temporário do usuário corrente da máquina com o nome do
	 * arquivo informado, aplicando as configurações passadas.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTA:</b> As colunas do tipo data receberão o formato
	 * {@link br.com.afirmanet.core.util.DateUtils#DEFAULT_TIMESTAMP_PATTERN} e as colunas do tipo decimal receberão o
	 * formato {@link br.com.afirmanet.core.util.BigDecimalUtils#DEFAULT_DECIMAL_PATTERN}.
	 * </p>
	 * 
	 * <pre>
	 * CsvUtils.writer(';', '&quot;', Charset.forName(&quot;UTF-8&quot;), &quot;teste.csv&quot;, new ResultSetImpl())
	 * </pre>
	 * 
	 * @param delimiter
	 *        um <code>Character</code> que representa o delimitador de campos dos registros
	 * @param quote
	 *        um <code>Character</code> que representa o delimitador de dados para cada campo
	 * @param charset
	 *        um <code>Charset</code> que representa o encoding utilizado no arquivo
	 * @param outputFileName
	 *        uma <code>String</code> que representa o nome do arquivo a ser gerado no diretório temporário da máquina
	 * @param resultSet
	 *        um <code>ResultSet</code> que detém o conteúdo a ser escrito no arquivo
	 * 
	 * @return um <code>File</code> contendo o arquivo gerado
	 * 
	 */
	public static File writer(final Character delimiter, final Character quote, final Charset charset, final String outputFileName, final ResultSet resultSet) {
		File file = new File(IoUtils.TEMP_FILE + outputFileName);
		writer(delimiter, quote, charset, file, resultSet);

		return file;
	}

	/**
	 * <p>
	 * Escreve o conteúdo do array informado no arquivo passado por parâmetro, aplicando as configurações passadas.
	 * </p>
	 * 
	 * <pre>
	 * CsvUtils.writer(';', '&quot;', Charset.forName(&quot;UTF-8&quot;), new File(&quot;C:\\teste.csv&quot;), new String[] { &quot;Primeira Linha&quot;, &quot;Segunda Linha&quot; })
	 * </pre>
	 * 
	 * @param delimiter
	 *        um <code>Character</code> que representa o delimitador de campos dos registros
	 * @param quote
	 *        um <code>Character</code> que representa o delimitador de dados para cada campo
	 * @param charset
	 *        um <code>Charset</code> que representa o encoding utilizado no arquivo
	 * @param outputFile
	 *        um <code>File</code> que receberá o conteúdo do resultSet, lembrando que o mesmo deve possuir o caminho
	 *        completo do arquivo e não somente o nome do arquivo
	 * @param values
	 *        um <code>String[]</code> que detém o conteúdo a ser escrito no arquivo
	 * 
	 */
	public static void writer(final Character delimiter, final Character quote, final Charset charset, final File outputFile, final String[] values) {
		CsvWriter csvWriter = new CsvWriter(new CsvConfig(delimiter, quote, charset));
		csvWriter.writer(outputFile, values);
	}

	/**
	 * <p>
	 * Escreve o conteúdo do array informado no arquivo passado por parâmetro, aplicando as configurações passadas.
	 * </p>
	 * 
	 * <pre>
	 * CsvUtils.writer(';', '&quot;', Charset.forName(&quot;UTF-8&quot;), &quot;teste.csv&quot;, new String[] { &quot;Primeira Linha&quot;, &quot;Segunda Linha&quot; }) = TEMP_DIRECTOY / teste.csv
	 * </pre>
	 * 
	 * @param delimiter
	 *        um <code>Character</code> que representa o delimitador de campos dos registros
	 * @param quote
	 *        um <code>Character</code> que representa o delimitador de dados para cada campo
	 * @param charset
	 *        um <code>Charset</code> que representa o encoding utilizado no arquivo
	 * @param outputFileName
	 *        uma <code>String</code> que representa o nome do arquivo a ser gerado no diretório temporário da máquina
	 * @param values
	 *        um <code>String[]</code> que detém o conteúdo a ser escrito no arquivo
	 * 
	 * @return um <code>File</code> contendo o arquivo gerado
	 * 
	 */
	public static File writer(final Character delimiter, final Character quote, final Charset charset, final String outputFileName, final String[] values) {
		File file = new File(IoUtils.TEMP_FILE + outputFileName);
		writer(delimiter, quote, charset, file, values);

		return file;
	}

}
