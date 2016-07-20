package br.com.afirmanet.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import lombok.SneakyThrows;
import br.com.afirmanet.core.exception.SystemException;
import br.com.afirmanet.core.io.enumeration.FileTypeEnum;

/**
 * <p>
 * Classe utilitária para compactação/descompactação de arquivos no formato <code>.zip</code>.
 * </p>
 */
public final class ZipUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private ZipUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	@SneakyThrows(IOException.class)
	public static void compressFiles(Collection<File> sourceFiles, File targetFile, byte[] buffer, Boolean enabledCheckedSum) {
		CheckedOutputStream checkedOutputStream = null;
		BufferedOutputStream bufferedOutputStream = null;

		try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {

			if (enabledCheckedSum) {
				checkedOutputStream = new CheckedOutputStream(fileOutputStream, new Adler32());
				bufferedOutputStream = new BufferedOutputStream(checkedOutputStream, buffer.length);
			} else {
				bufferedOutputStream = new BufferedOutputStream(fileOutputStream, buffer.length);
			}

			try (ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {

				for (File sourceFile : sourceFiles) {

					try (FileInputStream fileInputStream = new FileInputStream(sourceFile); BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, buffer.length)) {

						ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
						zipOutputStream.putNextEntry(zipEntry);
						zipOutputStream.setMethod(ZipOutputStream.DEFLATED);

						int bytesRead = 0;
						while ((bytesRead = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) { //NOPMD
							zipOutputStream.write(buffer, 0, bytesRead);
						}

					}
				}
			}

		} finally {
			IoUtils.closeQuietly(bufferedOutputStream);
			IoUtils.closeQuietly(checkedOutputStream);

		}
	}

	public static void compressFiles(Collection<File> sourceFiles, File targetFile) {
		compressFiles(sourceFiles, targetFile, IoUtils.DEFAULT_BUFFER, false);
	}

	/**
	 * <p>
	 * Compacta o <code>File</code> informado, salvando-o no local indicado, utilizando o <code>byte[]</code> para
	 * leitura e escrita passado como parâmetro.
	 * </p>
	 *
	 * <pre>
	 * ZipUtils.compress(new File(&quot;C:\\teste.txt&quot;), new File(&quot;C:\\teste-zipado.zip&quot;), new byte[1024])                    = Arquivo compactado em C:\teste-zipado.zip
	 * ZipUtils.compress(new File(&quot;C:\\teste-false.txt&quot;), new File(&quot;C:\\teste-false-zipado.zip&quot;), new byte[1024], false) = Arquivo compactado em C:\teste-false-zipado.zip
	 * ZipUtils.compress(new File(&quot;C:\\teste-true.txt&quot;), new File(&quot;C:\\teste-true-zipado.zip&quot;), new byte[1024], true)    = Arquivo compactado em C:\teste-true-zipado.zip
	 * ZipUtils.compress(new File(&quot;C:\\teste-null.txt&quot;), new File(&quot;C:\\teste-null-zipado.zip&quot;), new byte[1024], null)    = Arquivo compactado em C:\teste-null-zipado.zip
	 * </pre>
	 *
	 * @param sourceFile
	 *        o <code>File</code> que será compactado
	 * @param targetFile
	 *        o <code>File</code> que será destino do arquivo compactado
	 * @param buffer
	 *        um <code>byte[]</code> que será utilizado para os procedimentos de leitura e escrita do arquivo
	 * @param enabledCheckedSum
	 *        um <code>Boolean</code> que indica se será necessário a geração de checksum para o arquivo compactado
	 *
	 * @throws SystemException
	 *         se ocorrer algum erro de I/O
	 */
	public static void compress(File sourceFile, File targetFile, byte[] buffer, Boolean enabledCheckedSum) {
		if (!sourceFile.exists()) {
			throw new SystemException("Arquivo [" + sourceFile.getName() + "] utilizado para compactação não encontrado!");
		}

		compressFiles(Arrays.asList(sourceFile), targetFile, buffer, enabledCheckedSum);
	}

	public static void compress(File sourceFile, File targetFile, byte[] buffer) {
		compress(sourceFile, targetFile, buffer, false);
	}

	/**
	 * <p>
	 * Compacta o <code>File</code> informado, salvando-o no local indicado, utilizando o buffer para leitura e escrita
	 * padrão {@link IoUtils#DEFAULT_BUFFER}.
	 * </p>
	 *
	 * <pre>
	 * ZipUtils.compress(new File(&quot;C:\\teste.txt&quot;), new File(&quot;C:\\teste-zipado.zip&quot;))                    = Arquivo compactado em C:\teste-zipado.zip
	 * ZipUtils.compress(new File(&quot;C:\\teste-false.txt&quot;), new File(&quot;C:\\teste-false-zipado.zip&quot;), false) = Arquivo compactado em C:\teste-false-zipado.zip
	 * ZipUtils.compress(new File(&quot;C:\\teste-true.txt&quot;), new File(&quot;C:\\teste-true-zipado.zip&quot;), true)    = Arquivo compactado em C:\teste-true-zipado.zip
	 * ZipUtils.compress(new File(&quot;C:\\teste-null.txt&quot;), new File(&quot;C:\\teste-null-zipado.zip&quot;), null)    = Arquivo compactado em C:\teste-null-zipado.zip
	 * </pre>
	 *
	 * @param sourceFile
	 *        o <code>File</code> que será compactado
	 * @param targetFile
	 *        o <code>File</code> que será destino do arquivo compactado
	 * @param enabledCheckedSum
	 *        um <code>Boolean</code> que indica se será necessário a geração de checksum para o arquivo compactado
	 *
	 * @throws SystemException
	 *         se ocorrer algum erro de I/O
	 */
	public static void compress(File sourceFile, File targetFile, Boolean enabledCheckedSum) {
		compress(sourceFile, targetFile, IoUtils.DEFAULT_BUFFER, enabledCheckedSum);
	}

	public static void compress(File sourceFile, File targetFile) {
		compress(sourceFile, targetFile, IoUtils.DEFAULT_BUFFER, false);
	}

	/**
	 * <p>
	 * Compacta o <code>File</code> informado, salvando-o no mesmo local, porém com a extensão <b>.zip</b>.
	 * </p>
	 *
	 * <pre>
	 * ZipUtils.compress(new File(&quot;C:\\teste.txt&quot;))              = Arquivo compactado em C:\teste.zip
	 * ZipUtils.compress(new File(&quot;C:\\teste-false.txt&quot;), false) = Arquivo compactado em C:\teste-false.zip
	 * ZipUtils.compress(new File(&quot;C:\\teste-true.txt&quot;), true)   = Arquivo compactado em C:\teste-true.zip
	 * ZipUtils.compress(new File(&quot;C:\\teste-null.txt&quot;), null)   = Arquivo compactado em C:\teste-null.zip
	 * </pre>
	 *
	 * @param sourceFile
	 *        o <code>File</code> que será compactado
	 * @param enabledCheckedSum
	 *        um <code>Boolean</code> que indica se será necessário a geração de checksum para o arquivo compactado
	 *
	 * @return um <code>File</code> que representa o arquivo compactado
	 *
	 * @throws SystemException
	 *         se ocorrer algum erro de I/O
	 */
	public static File compress(File sourceFile, Boolean enabledCheckedSum) {
		String targetFile = sourceFile.getPath().substring(0, sourceFile.getPath().length() - 4) + FileTypeEnum.ZIP.getExtension();

		compress(sourceFile, new File(targetFile), enabledCheckedSum);

		return new File(targetFile);
	}

	public static File compress(File sourceFile) {
		return compress(sourceFile, false);
	}

	public static byte[] unzip(File zippedFile) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ZipFile zipFile = new ZipFile(zippedFile)) {

			Enumeration<?> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}

				try (InputStream inputStream = zipFile.getInputStream(entry)) {
					IoUtils.copy(inputStream, byteArrayOutputStream);
				}
			}

			return byteArrayOutputStream.toByteArray();

		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * <p>
	 * Descompacta o <code>File</code> zipado no local indicado.
	 * </p>
	 *
	 * <pre>
	 * ZipUtils.unzip(new File("C:\\teste.zip"), new File("C:\\teste-25042011.txt")) = Arquivo descompactado em C:\teste-25042011.txt
	 * </pre>
	 *
	 * @param zippedFile
	 *        um <code>File</code> que representa o arquivo zipado
	 * @param targetFile
	 *        um <code>File</code> que representa o arquivo de destino do conteúdo descompactado
	 *
	 * @throws SystemException
	 *         se ocorrer algum erro de I/O
	 */
	public static void unzip(File zippedFile, File targetFile) {
		try (OutputStream outputStream = new FileOutputStream(targetFile)) {
			outputStream.write(unzip(zippedFile));

		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * <p>
	 * Descompacta todos os arquivos com extensão <i>.zip</i> que estão no diretório informado, permanecendo o mesmo
	 * nome após sua descompactação, mudando apenas a extensão do arquivo de acordo o informado.
	 * </p>
	 *
	 * <pre>
	 * ZipUtils.unzip(new File("C:\\teste\\"), FileType.XML) = Arquivo(s) descompactado(s) em C:\teste\\
	 * </pre>
	 *
	 * @param directory
	 *        um <code>File</code> que representa o diretório que contém os arquivos zipados
	 * @param fileType
	 *        um enum <code>FileType</code> que define a extensão do arquivo pós descompactação
	 *
	 * @throws SystemException
	 *         se ocorrer algum erro de I/O
	 */
	public static void unzipFiles(File directory, FileTypeEnum fileType) {
		Collection<File> zippedFiles = FileUtils.listFiles(directory, new String[] { "zip" }, false);
		for (File zippedFile : zippedFiles) {
			File targetFile = new File(zippedFile.getPath().substring(0, zippedFile.getPath().length() - 4) + fileType.getExtension());
			unzip(zippedFile, targetFile);
		}
	}

	@SneakyThrows(IOException.class)
	public static List<File> unzipFileToDirectory(File zippedFile, File targetDirectory) {
		// Cria o diretório informado caso o mesmo não exista
		if (!targetDirectory.exists()) {
			if (!targetDirectory.mkdirs()) {
				throw new SystemException("Erro ao criar o diretório: %s", targetDirectory.getName());
			}
		}

		// Verifica se o diretório informado é válido
		if (!targetDirectory.exists() || !targetDirectory.isDirectory()) {
			throw new SystemException(String.format("O diretório %s não é válido!", targetDirectory.getName()));
		}

		List<File> unzippedFiles = new ArrayList<>();

		try (ZipFile zipFile = new ZipFile(zippedFile)) {

			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {
				ZipEntry zipEntry = e.nextElement();
				File inputFile = new File(targetDirectory, zipEntry.getName());

				// Caso seja diretório inexsitente, cria a estrutura e continua com a próxima entrada
				if (zipEntry.isDirectory() && !inputFile.exists()) {
					if (!inputFile.mkdirs()) {
						throw new SystemException("Erro ao criar o diretório: %s", inputFile.getName());
					}
					continue;
				}

				// Se a estrutura de diretórios não existe, cria-se toda estrutura
				if (!inputFile.getParentFile().exists()) {
					if (!inputFile.getParentFile().mkdirs()) {
						throw new SystemException("Erro ao criar o diretório: %s", inputFile.getParentFile().getName());
					}
				}

				try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {

					if (inputStream == null) {
						throw new ZipException(String.format("Erro ao ler a entrada do zip [%s]!", zipEntry.getName()));
					}

					try (OutputStream outputStream = new FileOutputStream(inputFile)) {
						IoUtils.copy(inputStream, outputStream);
					}

				}

				unzippedFiles.add(inputFile);
			}

			return unzippedFiles;
		}
	}

	@SneakyThrows(IOException.class)
	public static byte[] compressFileToMemory(File file) {
		if (!FileUtils.exists(file)) {
			throw new FileNotFoundException("Arquivo [" + file.getName() + "] utilizado para compactacao não encontrado!");
		}
		if (!file.isFile()) {
			throw new ZipException(file.getName() + " não é um arquivo.");
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos); BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()))) {

			zos.putNextEntry(new ZipEntry(file.getName()));
			int nBytes;
			byte[] tmpBuf = new byte[8 * 1024];

			while ((nBytes = bis.read(tmpBuf)) > 0) {
				zos.write(tmpBuf, 0, nBytes);
			}
			zos.closeEntry();

			return baos.toByteArray();
		}
	}

	@SneakyThrows(IOException.class)
	public static byte[] compressFilesToMemory(Collection<Path> pathList) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

			for (Path path : pathList) {
				ZipEntry zipentry = new ZipEntry(path.getFileName().toString());
				zipOutputStream.putNextEntry(zipentry);
				zipOutputStream.write(Files.readAllBytes(path));
			}

			byte[] byteArray = byteArrayOutputStream.toByteArray();
			return byteArray;
		}
	}

	@SneakyThrows(IOException.class)
	public static byte[] compressDirectoryToMemory(File directory) {
		if (!FileUtils.exists(directory)) {
			throw new FileNotFoundException("Diretório [" + directory.getName() + "] utilizado para compactacao não encontrado!");
		}
		if (!directory.isDirectory()) {
			throw new ZipException(directory.getName() + " não é um diretório.");
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {
			addDirectory(directory, zos, directory.getName());
		}

		return baos.toByteArray();
	}

	@SneakyThrows(IOException.class)
	private static void addDirectory(File dirObj, ZipOutputStream out, String nameDirectory) {
		byte[] tmpBuf = new byte[8 * 1024];

		for (File file : dirObj.listFiles()) {
			if (file.isDirectory()) {
				addDirectory(file, out, nameDirectory);
				continue;
			}

			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()))) {
				int nBytes;

				String filePath = file.getAbsolutePath();
				out.putNextEntry(new ZipEntry(filePath.substring(filePath.indexOf(nameDirectory))));

				while ((nBytes = bis.read(tmpBuf)) > 0) {
					out.write(tmpBuf, 0, nBytes);

				}
				out.closeEntry();
			}
		}
	}

}
