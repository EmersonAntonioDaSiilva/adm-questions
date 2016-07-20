package br.com.afirmanet.core.io.csv;

/**
 * <p>
 * Detém as funcionalidades comuns às classes que manupulam arquivo do tipo <b>CSV</b>.
 * </p>
 */
public abstract class AbstractCsv { //NOPMD

	/**
	 * <p>
	 * Buffer utilizado para leitura e escrita dos arquivos no formato .csv.
	 * </p>
	 */
	protected byte[] buffer;

	/**
	 * <p>
	 * Objeto que detém toda a configuração para leitura e escrita de arquivo .csv, bem como: caracter delimitador,
	 * charset, etc...
	 * </p>
	 */
	protected CsvConfig csvConfig;

}
