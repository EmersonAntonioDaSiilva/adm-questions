package br.com.afirmanet.core.io.csv;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import br.com.afirmanet.core.constant.CharsetConstants;
import br.com.afirmanet.core.exception.SystemException;
import br.com.afirmanet.core.util.IoUtils;

/**
 * <p>
 * Detém as funcionalidades necessárias para escrita de arquivos no formato .csv.
 * </p>
 */
@Slf4j
public class CsvWriter extends AbstractCsv {

	/**
	 * <p>
	 * Construtor que permite a definição das configurações a serem utilizadas na escrita do arquivo.
	 * </p>
	 *
	 * @param csvConfig
	 *        um <code>CsvConfig</code> que representa as configurações que serão empregadas no processamento do arquivo
	 */
	public CsvWriter(CsvConfig csvConfig) {
		super();
		this.csvConfig = csvConfig;
		buffer = IoUtils.DEFAULT_BUFFER;
	}

	/**
	 * <p>
	 * Construtor utilizado somente pelas classes do mesmo pacote.
	 * </p>
	 *
	 * @param csvConfig
	 *        um <code>CsvConfig</code> que representa as configurações que serão empregadas no processamento do arquivo
	 * @param buffer
	 *        um <code>buffer[]</code> utilizado para manipulação do conteúdo a ser escrito no arquivo
	 */
	CsvWriter(CsvConfig csvConfig, byte[] buffer) {
		super();
		this.csvConfig = csvConfig;
		this.buffer = buffer.clone();
	}

	/**
	 * <p>
	 * Itera sobre o array de Strings passado por parâmetro, escrevendo-o no arquivo informado.
	 * </p>
	 *
	 * @param file
	 *        um <code>File</code> que receberá o conteúdo do <code>String[]</code>
	 * @param values
	 *        um <code>String[]</code> que detém o conteúdo a ser escrito no arquivo
	 */
	public void writer(File file, String[] values) {
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file), buffer.length)) {

			Integer currentLine = 0;

			for (String value : values) {
				log.debug("Escrevendo conteudo da linha [" + (++currentLine) + "]...");

				synchronized (buffer) {
					if (csvConfig.hasHeader() && currentLine == 1) {
						outputStream.write(csvConfig.getHeader().getBytes(CharsetConstants.UTF8));
						outputStream.write(IoUtils.END_LINE);
					}

					outputStream.write(value.getBytes(CharsetConstants.UTF8));
					outputStream.write(IoUtils.END_LINE);
				}
			}

			log.debug("Arquivo gerado em " + file.toString());

		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	public void writer(File file, ResultSet resultSet, String datePattern, String numberPattern) {
		writer(file, resultSet, datePattern, numberPattern, false, true);
	}

	/**
	 * <p>
	 * Escreve todo conteúdo do <code>ResultSet</code> passado por parâmetro no arquivo informado.
	 * </p>
	 *
	 * @param file
	 *        um <code>File</code> que receberá o conteúdo do <code>ResultSet</code>
	 * @param resultSet
	 *        um <code>ResultSet</code> que contém as inforamções que serão escritas no <code>File</code> informado
	 * @param datePattern
	 *        uma <code>String</code> que representa o formato a ser aplicado nos campos do tipo Data
	 *        <code>java.time.LocalDate</code> e <code>java.time.LocalDateTime</code>
	 * @param numberPattern
	 *        uma <code>String</code> que representa o formato a ser aplicado nos campos do tipo Decimal
	 *        <code>java.math.BigDecimal</code>
	 * @param append
	 *        se true, então os bytes serão escritos no final do arquivo ao invés do começo
	 * @param format
	 *        se true, formata as datas e números
	 */
	public void writer(File file, ResultSet resultSet, String datePattern, String numberPattern, Boolean append, Boolean format) {
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file, append), buffer.length)) {

			NumberFormat numberFormat = new DecimalFormat(numberPattern);
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);

			Integer columns = null;
			if (csvConfig.hasHeader()) {
				String[] columnFields = csvConfig.getHeader().split(Character.toString(csvConfig.getDelimiter()));
				columns = columnFields.length;
			} else {
				columns = resultSet.getMetaData().getColumnCount();
				csvConfig.setHeader(getHeader(resultSet));
			}

			String currentValue = "";
			Integer currentLine = 0;
			while (resultSet.next()) {
				log.debug("Escrevendo conteudo da linha [" + (++currentLine) + "]...");

				synchronized (buffer) {
					if (csvConfig.hasHeader() && currentLine == 1) {
						outputStream.write(csvConfig.getHeader().getBytes(CharsetConstants.UTF8));
						outputStream.write(IoUtils.END_LINE);
					}

					// Escreve o conteúdo
					for (int i = 1; i <= columns; i++) {
						Object value = resultSet.getObject(i);

						if (format) {
							if (value instanceof BigDecimal && resultSet.getMetaData().getScale(i) == 0) {
								value = ((BigDecimal) value).longValue();
							}
							currentValue = getFormattedValue(value, dateTimeFormatter, datePattern, numberFormat, numberPattern);
						} else if (value instanceof Clob) {
							Clob clob = (Clob) value;
							try (BufferedReader reader = new BufferedReader(clob.getCharacterStream())) {
								StringBuffer strBuf = new StringBuffer();

								String linha = null;
								while ((linha = reader.readLine()) != null) {
									strBuf.append(linha);
								}
								value = strBuf.toString();
								currentValue = getFormattedValue(value, dateTimeFormatter, datePattern, numberFormat, numberPattern);
							}
						} else {
							currentValue = ObjectUtils.defaultIfNull(value, StringUtils.EMPTY).toString();
						}

						log.debug("Coluna [" + resultSet.getMetaData().getColumnName(i) + "] --> Conteudo [" + currentValue + "]...");

						if (i == 1) {
							outputStream.write(currentValue.getBytes(CharsetConstants.UTF8));
						} else {
							outputStream.write(csvConfig.getDelimiter());

							if (currentValue != null) {
								outputStream.write(currentValue.getBytes(CharsetConstants.UTF8));
							}
						}
					}
					outputStream.write(IoUtils.END_LINE);
				}
			}

			log.debug("Arquivo gerado em " + file.toString());

		} catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * <p>
	 * Monta o cabeçalho do arquivo através do <code>ResultSet</code>, obtendo o nome das colunas contidas no
	 * <code>Resultset</code> passado por parâmetro.
	 * </p>
	 *
	 * @param resultSet
	 *        um <code>ResultSet</code> que detém as colunas oriundas de consulta no banco de dados
	 *
	 * @return uma <code>String</code> que representa o nome das colunas separadas pelo delimitador passado para o
	 *         objeto instanciado da classe <code>CsvConfig</code>
	 *
	 */
	private String getHeader(ResultSet resultSet) {
		try {
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			Integer columns = resultSetMetaData.getColumnCount();

			StringBuilder header = new StringBuilder();
			for (int i = 1; i <= columns; i++) {
				if (i == 1) {
					header.append(resultSetMetaData.getColumnName(i));
				} else {
					header.append(csvConfig.getDelimiter()).append(resultSetMetaData.getColumnName(i));
				}
			}

			return header.toString();

		} catch (SQLException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * <p>
	 * Retorna o valor passado por parâmetro formatado, de acordo com o padrão de formatação informado.
	 * </p>
	 *
	 * @param object
	 *        um <code>Object</code> que representa o valor a ser formatado
	 * @param simpleDateFormat
	 *        um <code>SimpleDateFormat</code> utilizado para formatar valores do tipo Data
	 * @param datePattern
	 *        uma <code>String</code> que representa o padrão de formatação a ser utilizado quando o <code>Object</code>
	 *        corresponder a uma Data
	 * @param numberFormat
	 *        um <code>NumberFormat</code> utilizado para formatar valores do tipo Decimal
	 * @param numberPattern
	 *        uma <code>String</code> que representa o padrão de formatação a ser utilizado quando o <code>Object</code>
	 *        corresponder a um Decimal
	 *
	 * @return valor formatado
	 */
	private String getFormattedValue(Object object, DateTimeFormatter dateTimeFormatter, String datePattern, NumberFormat numberFormat, String numberPattern) {
		String currentValue = "";

		if (StringUtils.isNotEmpty(datePattern) && object instanceof java.time.LocalDate) {
			currentValue = ((java.time.LocalDate) object).format(dateTimeFormatter);
		} else if (StringUtils.isNotEmpty(datePattern) && object instanceof java.time.LocalDateTime) {
			currentValue = ((java.time.LocalDateTime) object).format(dateTimeFormatter);
		} else if (StringUtils.isNotEmpty(numberPattern) && object instanceof java.math.BigDecimal) {
			currentValue = numberFormat.format(object);
		} else if (object instanceof java.lang.Long) {
			currentValue = ObjectUtils.defaultIfNull(object, StringUtils.EMPTY).toString();
		} else {
			currentValue = (String) ObjectUtils.defaultIfNull(object, StringUtils.EMPTY);
		}

		return currentValue;
	}

}
