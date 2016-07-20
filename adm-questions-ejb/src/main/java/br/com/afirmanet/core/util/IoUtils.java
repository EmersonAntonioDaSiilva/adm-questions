package br.com.afirmanet.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.activation.DataHandler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

import br.com.afirmanet.core.constant.CharsetConstants;

/**
 * <p>
 * Classe utilitária para manipulação de funcionalidades comuns de IO (entrada/saída).
 * </p>
 */
@Slf4j
public final class IoUtils extends org.apache.commons.io.IOUtils {

	/**
	 * <p>
	 * Tamanho do buffer para leitura e escrita do arquivo.
	 * </p>
	 */
	public static final byte[] DEFAULT_BUFFER = new byte[1000000];

	/**
	 * <p>
	 * Caminho completo do diretório temporário do usuário corrente no sistema operacional.
	 * </p>
	 */
	public static final String TEMP_FILE = FileUtils.getTempDirectory().getPath() + "\\";

	/**
	 * <p>
	 * Bytes da string que representa quebra de linha <code>'\n'</code> em texto.
	 * </p>
	 */
	public static final byte[] END_LINE = "\n".getBytes(CharsetConstants.UTF8);

	/**
	 * <p>
	 * Charset padrão para manipulação de arquivos.
	 * </p>
	 */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private IoUtils() {
		super();
	}

	/**
	 * <p>
	 * Fecha o stream de entrada passado por parâmetro. Caso o mesmo seja nulo, é desconsiderado seu fechamento.
	 * </p>
	 *
	 * @param inputStream
	 *        um <code>InputStream</code> para ser fechado
	 *
	 */
	public static void closeQuietly(InputStream inputStream) {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Fecha o stream de saída passado por parâmetro. Caso o mesmo seja nulo, é desconsiderado seu fechamento.
	 * </p>
	 *
	 * @param outputStream
	 *        um <code>OutputStream</code> para ser fechado
	 *
	 */
	public static void closeQuietly(OutputStream outputStream) {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * Obtém o conteúdo do <code>DataHandler</code> informado, passando-o para um <code>byte[]</code>.
	 * <p>
	 *
	 * @param dataHandler
	 *        o <code>DataHandler</code> que terá seu conteúdo lido
	 *
	 * @return um <code>byte[]</code>
	 *
	 * @throws NullPointerException
	 *         se o <code>DataHandler</code> for <code>null</code>
	 */
	@SneakyThrows(IOException.class)
	public static byte[] toByteArray(DataHandler dataHandler) {
		try (InputStream inputStream = dataHandler.getInputStream()) {

			byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);
			return bytes;

		}
	}

	/**
	 * <p>
	 * Escreve os bytes contidos no <code>byte[]</code> para um <code>File</code>.
	 * </p>
	 *
	 * @param bytes
	 *        um <code>byte[]</code> para escrita que não pode ser modificado durante a execução
	 * @param file
	 *        um <code>File</code> que receberá o <code>byte[]</code>
	 *
	 * @throws NullPointerException
	 *         se file for <code>null</code>
	 *
	 */
	@SneakyThrows(IOException.class)
	public static void write(byte[] bytes, File file) {
		org.apache.commons.io.IOUtils.write(bytes, new FileOutputStream(file));
	}

	@SneakyThrows(IOException.class)
	public static long countLines(byte[] bytes) {
		try (InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(bytes), CharsetConstants.UTF8);
				LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader)) {
			return lineNumberReader.lines().count();
		}
	}

}
