package br.com.afirmanet.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.SneakyThrows;

import org.apache.commons.io.FilenameUtils;

import br.com.afirmanet.core.bean.DownloadFile;
import br.com.afirmanet.core.bean.FileNode;
import br.com.afirmanet.core.exception.FileRepositoryException;
import br.com.afirmanet.core.exception.SystemException;
import br.com.afirmanet.core.io.enumeration.FileTypeEnum;

public final class FileRepositoryUtils {

	private static final String ZIP = "zip";
	private static final Character UNDERSCORE = '_';
	private static final String DOT = ".";

	private FileRepositoryUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	public static List<FileNode> loadRepository(File repository) {
		if (!FileUtils.exists(repository)) {
			throw new FileRepositoryException("Repositório " + repository.getAbsoluteFile() + " Inexistente");
		}

		FileNode fileNode = new FileNode();
		fileNode.setName(repository.getName());
		fileNode.setPath(repository.getPath());
		fileNode.setNodes(searchNodes(repository));

		List<FileNode> root = new ArrayList<>();
		root.add(fileNode);
		return root;
	}

	public static List<FileNode> searchFiles(File directory) {

		if (!FileUtils.exists(directory) || directory.isFile()) {
			return null;
		}

		List<FileNode> files = new ArrayList<>();

		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				files.add(new FileNode(file.getName(), file.getAbsolutePath(), LocalDateTime.from(Instant.ofEpochMilli(file.lastModified())), FileUtils.byteCountToDisplaySize(file.length()), null));
			}
		}

		return files;
	}

	public static DownloadFile downloadFile(File file, Integer maximumFileSizeMB) {
		if (!FileUtils.exists(file)) {
			throw new FileRepositoryException("Arquivo " + file.getAbsolutePath() + " Inexistente.");
		}

		if (file.isDirectory() && !FileUtils.directoryHasFile(file)) {
			throw new FileRepositoryException("Diretório " + file.getAbsolutePath() + " não possui arquivos.");
		}

		try {
			DownloadFile downloadFile = new DownloadFile();
			byte[] fileContent = null;

			if (file.isDirectory()) {
				fileContent = ZipUtils.compressDirectoryToMemory(file);
				downloadFile.setFileName(FileUtils.getTempName(FileTypeEnum.ZIP));
			} else {
				fileContent = FileUtils.readFileToByteArray(file);
				downloadFile.setFileName(file.getName());
			}

			if (fileContent.length < maximumFileSizeMB * FileUtils.ONE_MB) {
				downloadFile.setFileContent(fileContent);
			}

			return downloadFile;
		} catch (Exception e) {
			throw new FileRepositoryException(e);
		}
	}

	public static DownloadFile downloadFile(String path, Integer maximumFileSizeMB) {
		return downloadFile(new File(path), maximumFileSizeMB);
	}

	public static synchronized boolean deleteFile(File file, String repositoryPath, String repositoryBackupPath) {
		if (!FileUtils.exists(file)) {
			throw new FileRepositoryException("Arquivo " + file.getAbsolutePath() + " Inexistente.");
		}

		try {
			deleteBackupFile(file, file.getParent().replace(repositoryPath, repositoryBackupPath));
			backupFile(file, repositoryPath, repositoryBackupPath);
			if (!file.delete()) {
				throw new SystemException("Erro ao excluir o arquivo: %s", file.getName());
			}
			return true;
		} catch (Exception e) {
			throw new FileRepositoryException(e);
		}
	}

	public static synchronized void upload(File directoryUpload, List<DownloadFile> downloadFileList, String repositoryPath, String repositoryBackupPath) {
		File file = null;
		try {
			for (DownloadFile downloadFile : downloadFileList) {
				if (FilenameUtils.isExtension(downloadFile.getFileName().toLowerCase(), ZIP)) {
					file = new File(FileUtils.getTempDirectoryPath(), FilenameUtils.getName(downloadFile.getFileName()));
					FileUtils.writeByteArrayToFile(file, downloadFile.getFileContent());
					uploadZipFile(directoryUpload, file, repositoryPath, repositoryBackupPath);
				} else {
					File fileUpload = new File(directoryUpload, FilenameUtils.getName(downloadFile.getFileName()));
					if (fileUpload.exists()) {
						backupFile(fileUpload, repositoryPath, repositoryBackupPath);
					}
					FileUtils.writeByteArrayToFile(fileUpload, downloadFile.getFileContent());
				}
			}
		} catch (Exception e) {
			throw new FileRepositoryException(e);
		} finally {
			if (file != null && file.exists()) {
				FileUtils.deleteQuietly(file);
			}
		}
	}

	private static List<FileNode> searchNodes(File repository) {
		List<FileNode> directories = new ArrayList<>();

		for (File file : repository.listFiles()) {
			if (file.isDirectory()) {
				directories.add(new FileNode(file.getName(), file.getAbsolutePath(), LocalDateTime.from(Instant.ofEpochMilli(file.lastModified())), FileUtils.byteCountToDisplaySize(file.length()),
						searchNodes(file)));
			}
		}

		return directories;
	}

	private static void deleteBackupFile(File file, String parentPath) {
		File directory = new File(parentPath);

		if (!directory.exists()) {
			return;
		}

		for (File entry : directory.listFiles()) {
			if (entry.isFile()) {
				String entryName = entry.getName().substring(0, entry.getName().lastIndexOf(UNDERSCORE));
				String extensionEntry = FilenameUtils.getExtension(entry.getName());

				String extensionFile = FilenameUtils.getExtension(file.getName());
				String fileName = file.getName().replace(StringUtils.isNotBlank(extensionFile) ? DOT + extensionFile : extensionFile, "");

				if (entryName.equals(fileName) && extensionEntry.equals(extensionFile)) {
					if (!entry.delete()) {
						throw new SystemException("Erro ao excluir o arquivo: %s", entry.getName());
					}
				}
			}
		}

	}

	@SneakyThrows(IOException.class)
	private static void uploadZipFile(File directoryUpload, File file, String repositoryPath, String repositoryBackupPath) {
		try (ZipFile zipfile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> zipEntries = zipfile.entries();
			while (zipEntries.hasMoreElements()) {
				ZipEntry entry = zipEntries.nextElement();
				if (entry.isDirectory()) {
					if (!new File(directoryUpload, entry.getName()).mkdirs()) {
						throw new SystemException("Erro ao criar o diretório: %s", entry.getName());
					}
					continue;
				}

				File destFile = new File(directoryUpload, entry.getName());

				if (destFile.exists()) {
					backupFile(destFile, repositoryPath, repositoryBackupPath);
				}

				FileOutputStream fos = new FileOutputStream(destFile);

				try (InputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry)); OutputStream outputStream = new BufferedOutputStream(fos)) {
					IoUtils.copy(inputStream, outputStream);
					outputStream.flush();
				}
			}
		}
	}

	private static synchronized void backupFile(File file, String repositoryPath, String repositoryBackupPath) {
		if (file == null || !file.exists()) {
			throw new FileRepositoryException("Arquivo Inexistente.");
		}

		try {
			String parentPath = file.getParent().replace(repositoryPath, repositoryBackupPath);
			String extension = FilenameUtils.getExtension(file.getName());
			if (StringUtils.isNotBlank(extension)) {
				extension = DOT + extension;
			}
			String newFilename = file.getName().replace(extension, "") + UNDERSCORE + DateUtils.format(LocalDateTime.now(), DateUtils.DATE_PATTERN_YYYYMMDDHHmmss) + extension;

			File destFile = new File(parentPath, newFilename);
			FileUtils.copyFile(file, destFile);
		} catch (Exception e) {
			throw new FileRepositoryException(e);
		}
	}

}