package br.com.afirmanet.core.web.util;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;

import br.com.afirmanet.core.enumeration.ContentTypeEnum;
import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.exception.SystemException;
import br.com.afirmanet.core.io.enumeration.FileTypeEnum;
import br.com.afirmanet.core.util.IoUtils;

/**
 * <p>
 * Classe utilitária para atachamento/anexo de arquivos.
 * </p>
 */
public final class AttachamentUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private AttachamentUtils() {
		// Não faz sentido instanciar classe utilitária
	}

	@SneakyThrows(IOException.class)
	public static void attach(HttpServletResponse response, String fileType, byte[] bytes, String fileName, boolean addHeader) {
		response.reset();
		response.setContentLength(bytes.length);

		if (addHeader) {
			response.addHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
		} else {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		}
		response.setContentType("application/" + fileType);

		try {
			response.getOutputStream().write(bytes, 0, bytes.length);

		} finally {
			response.getOutputStream().flush();
			IoUtils.closeQuietly(response.getOutputStream());
		}
	}

	/**
	 * <p>
	 * Anexa o conteúdo do arquivo passado por parâmetro no response informado, possibilitando o download do mesmo.
	 * </p>
	 *
	 * @param response
	 *        response que receberá os bytes lidos do arquivo passado por parâmetro
	 * @param fileType
	 *        tipo do anexo
	 * @param bytes
	 *        bytes do conteúdo do arquivo
	 * @param fileName
	 *        nome do arquivo
	 * @param addHeader
	 *        indica se o arquivo é para ser anexado em outro header do response informado
	 */
	public static void attach(HttpServletResponse response, FileTypeEnum fileType, byte[] bytes, String fileName, boolean addHeader) {
		try {
			response.reset();
			response.setContentLength(bytes.length);

			if (addHeader) {
				response.addHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
			} else {
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			}

			if (FileTypeEnum.PDF == fileType) {
				response.setContentType(ContentTypeEnum.PDF.getValue());
			} else if (FileTypeEnum.XLS == fileType) {
				response.setContentType(ContentTypeEnum.XLS.getValue());
			} else if (FileTypeEnum.DOC == fileType) {
				response.setContentType(ContentTypeEnum.DOC.getValue());
			} else if (FileTypeEnum.TXT == fileType) {
				response.setContentType(ContentTypeEnum.TXT.getValue());
			} else if (FileTypeEnum.CSV == fileType) {
				response.setContentType(ContentTypeEnum.CSV.getValue());
			} else if (FileTypeEnum.ZIP == fileType) {
				response.setContentType(ContentTypeEnum.ZIP.getValue());
			} else if (FileTypeEnum.XML == fileType) {
				response.setContentType(ContentTypeEnum.XML.getValue());
			} else {
				throw new ApplicationException("Anexo para arquivo de extensão %s não suportado!", fileType.getExtension());
			}

			try {
				response.getOutputStream().write(bytes, 0, bytes.length);

			} finally {
				response.getOutputStream().flush();
				IoUtils.closeQuietly(response.getOutputStream());
			}

		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * <p>
	 * Anexa o conteúdo do arquivo passado por parâmetro no response informado, possibilitando o download do mesmo.
	 * </p>
	 *
	 * @param response
	 *        response que receberá os bytes lidos do arquivo passado por parâmetro
	 * @param fileType
	 *        tipo do anexo
	 * @param bytes
	 *        bytes do conteúdo do arquivo
	 * @param fileName
	 *        nome do arquivo
	 */
	public static void attach(HttpServletResponse response, FileTypeEnum fileType, byte[] bytes, String fileName) {
		attach(response, fileType, bytes, fileName, false);
	}

	/**
	 * <p>
	 * Anexa o arquivo passado por parâmetro no response informado, possibilitando o download do mesmo.
	 * </p>
	 *
	 * @param response
	 *        Response que receberá os bytes lidos do arquivo passado por parâmetro.
	 * @param fileType
	 *        Tipo do anexo.
	 * @param file
	 *        Arquivo a ser anexado para download no response informado.
	 */
	public static void attach(HttpServletResponse response, FileTypeEnum fileType, File file) {
		try {
			byte[] bytes = FileUtils.readFileToByteArray(file);
			attach(response, fileType, bytes, file.getName());

		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	public static void attach(HttpServletResponse response, byte[] bytes, String fileName) {
		attach(response, br.com.afirmanet.core.util.FileUtils.getFileType(fileName), bytes, fileName);
	}

}
