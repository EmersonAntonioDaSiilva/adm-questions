package br.com.afirmanet.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import lombok.SneakyThrows;

import org.apache.commons.io.FileExistsException;

import br.com.afirmanet.core.constant.CharsetConstants;
import br.com.afirmanet.core.exception.SystemException;
import br.com.afirmanet.core.io.enumeration.FileTypeEnum;
import br.com.afirmanet.core.io.filter.ContainsFilter;

/**
 * <p>
 * Classe utilitária para manipulação de objetos <code>java.io.File</code>.
 * </p>
 *
 * <ul>
 * <li><b>generateTempName</b> - gera um nome temporário que pode ser atribuído a um arquivo</li>
 * </ul>
 *
 * @see java.io.File
 * @see org.apache.commons.io.FileUtils
 * @see FileTypeEnum
 *
 */
public final class FileUtils extends org.apache.commons.io.FileUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private FileUtils() {
		super();
	}

	/**
	 * <p>
	 * Gera um nome temporário que pode ser utilizado ao criar arquivos temporários.
	 * </p>
	 *
	 * <p>
	 * <b>NOTA:</b> Quando o tipo do arquivo não for informado, o nome será gerado sem a extensão.
	 * </p>
	 *
	 * <pre>
	 * FileUtils.getTempName(null)                             = 1303387798781011273077843398167
	 * FileUtils.getTempName(FileType.TXT)                     = 1303387798781017869044047588107.txt
	 * FileUtils.getTempName(FileType.DOC, "texto_do_prefixo") = texto_do_prefixo1303387798781015166188982259239.doc
	 * FileUtils.getTempName(null, "texto_do_prefixo")         = texto_do_prefixo130338779879601011431589108686246
	 * FileUtils.getTempName(FileType.CSV, "")                 = 1303387798796019280271099027394.csv
	 * FileUtils.getTempName(null, null)                       = 1303387798796016224949169027325
	 * </pre>
	 *
	 * @param fileType
	 *        um <code>FileType</code> que representa o tipo do arquivo, ou seja, qual extensão deverá ser acrescida no
	 *        nome gerado
	 * @param prefix
	 *        texto que pode ser acrescido como prefixo do nome gerado
	 *
	 * @return uma <code>String</code> que representa o nome gerado aleatóriamente
	 *
	 */
	public static String getTempName(final FileTypeEnum fileType, final String prefix) {
		String random = String.valueOf(Math.random()).replace('.', '1');
		String currentTimeMillis = String.valueOf(System.currentTimeMillis());

		StringBuilder temporaryFile = new StringBuilder(StringUtils.defaultString(prefix));
		temporaryFile.append(currentTimeMillis).append(random).append(fileType == null ? StringUtils.EMPTY : fileType.getExtension());

		return temporaryFile.toString();
	}

	public static String getTempName(final FileTypeEnum fileType) {
		return getTempName(fileType, StringUtils.EMPTY);
	}

	public static File createDirectoryIfNotExists(File directory) {
		if (directory != null && !directory.exists()) {
			if (!directory.mkdirs()) {
				throw new SystemException("Erro ao criar o diretório: %s", directory.getName());
			}
		}

		return directory;
	}

	@SneakyThrows(IOException.class)
	public static void moveFileToDirectory(File srcFile, File destDirectory, boolean override) {
		try {
			org.apache.commons.io.FileUtils.moveFileToDirectory(srcFile, destDirectory, false);

		} catch (FileExistsException e) {
			if (override) {
				File newFile = new File(destDirectory.getPath().concat(File.separator).concat(srcFile.getName()));
				FileUtils.deleteQuietly(newFile);

				org.apache.commons.io.FileUtils.moveFileToDirectory(srcFile, destDirectory, false);
			} else {
				throw new SystemException(e);
			}
		}
	}

	public static void moveFilesToDirectory(File srcDirectory, File destDirectory, String[] extensions, boolean override) {
		Collection<File> files = FileUtils.listFiles(srcDirectory, extensions, false);
		for (File file : files) {
			moveFileToDirectory(file, destDirectory, override);
		}
	}

	public static File[] listFiles(File directory, String filter) {
		return directory.listFiles(new ContainsFilter(filter));
	}

	public static FileTypeEnum getFileType(String fileName) {
		String fileExtension = "." + StringUtils.substringAfterLast(fileName, ".");
		FileTypeEnum fileType = FileTypeEnum.valueOfByFileExtension(fileExtension);

		return fileType;
	}

	public static boolean exists(File file) {
		return file != null && file.exists();
	}

	public static boolean directoryHasFile(File directory) {
		if (exists(directory)) {
			for (File file : directory.listFiles()) {
				if (file.isFile()) {
					return true;
				}
				directoryHasFile(file);
			}
		}
		return false;
	}

	public static File getParentFile(File file) {
		if (!exists(file) || file.isFile()) {
			return getParentFile(file.getParentFile());
		}
		return file;
	}

	public static int countLines(File filename) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), CharsetConstants.UTF8))) {
			int count = 0;
			while (reader.readLine() != null) {
				count++;
			}
			return count;

		} catch (IOException e) {
			throw new SystemException(e);
		}
	}
}
