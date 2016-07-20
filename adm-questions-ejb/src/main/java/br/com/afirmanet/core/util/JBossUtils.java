package br.com.afirmanet.core.util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.exception.SystemException;

public final class JBossUtils {

	public static final String PROPERTY_INSTANCE_NAME = "jboss.server.name";
	public static final String PROPERTY_TEMP_DIRECTORY = "jboss.server.temp.dir";
	public static final String PROPERTY_LOGS_DIRECTORY = "jboss.server.log.dir";
	public static final String PROPERTY_CURRENT_LOG_FILE = "server.log";

	@SneakyThrows(UnknownHostException.class)
	public static boolean isCorrectInstance(final String hostAddress, final String instanceName) {
		String localHostAddress = InetAddress.getLocalHost().getHostAddress();
		boolean isCorrectHostAddress = localHostAddress.equals(hostAddress);

		String currentInstanceName = JBossUtils.getCurrentInstanceName();
		boolean isCorrectInstanceName = currentInstanceName.equalsIgnoreCase(instanceName);

		return isCorrectHostAddress && isCorrectInstanceName;
	}

	public static File getLogFilesZipped(final LocalDateTime startDate, final LocalDateTime endDate, final Integer maximumSize) {
		try {
			File diretorioLogsServidor = new File(System.getProperty(PROPERTY_LOGS_DIRECTORY));

			// Verifica se o tamanho dos arquivos não causará OutOfMemory
			long calculatedMaximumSize = maximumSize * FileUtils.ONE_MB;
			long filesSize = FileUtils.sizeOf(diretorioLogsServidor);
			if (filesSize > calculatedMaximumSize) {
				throw new ApplicationException("Não foi possível ler os arquivos de log do servidor, pois o tamanho dos arquivos [{}] excede o máximo permitido [{}]",
						FileUtils.byteCountToDisplaySize(filesSize), FileUtils.byteCountToDisplaySize(calculatedMaximumSize));
			}

			// Retira os minutos, segundos e milesegundos das horas utilizadas para comparação com os arquivos de log
			LocalDateTime truncateStartDate = startDate.truncatedTo(ChronoUnit.HOURS);
			LocalDateTime truncateEndDate = endDate.truncatedTo(ChronoUnit.HOURS);

			// Seleção dos arquivos de logs
			List<File> selectedFiles = new ArrayList<>();
			File[] logFiles = FileUtils.listFiles(diretorioLogsServidor, PROPERTY_CURRENT_LOG_FILE);

			for (File logFile : logFiles) {
				if (PROPERTY_CURRENT_LOG_FILE.equals(logFile.getName())) {
					selectedFiles.add(logFile);

				} else {
					String source = StringUtils.substring(logFile.getName(), logFile.getName().length() - 13, logFile.getName().length());
					LocalDateTime logDate = DateUtils.parseLocalDateTime(source, "yyyy-MM-dd-HH");

					boolean add = logDate.compareTo(truncateStartDate) >= 0 && logDate.compareTo(truncateEndDate) <= 0;
					if (add) {
						selectedFiles.add(logFile);
					}
				}
			}

			if (selectedFiles.isEmpty()) {
				return null;
			}

			InetAddress inetAddress = InetAddress.getLocalHost();
			String currentInstanceName = JBossUtils.getCurrentInstanceName();
			String timestamp = DateUtils.format(LocalDateTime.now(), "yyyyMMdd_HHmmss");
			String fileName = String.format("%s_%s_%s.zip", inetAddress.getHostAddress(), currentInstanceName, timestamp);

			File zippedFile = new File(IoUtils.TEMP_FILE.concat(fileName));
			ZipUtils.compressFiles(selectedFiles, zippedFile);

			return zippedFile;

		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	public static String getCurrentInstanceName() {
		return System.getProperty(PROPERTY_INSTANCE_NAME);
	}

	public static File getTempDirectory() {
		return new File(System.getProperty(PROPERTY_TEMP_DIRECTORY));
	}

	// Fonte: https://developer.jboss.org/wiki/VFS3UserGuide
	@SneakyThrows(IOException.class)
	public static String getPhysicalFilePath(final String filePath) {
		URL url = JBossUtils.class.getResource(filePath);
		VirtualFile virtualFile = VFS.getChild(url.getPath());
		if (virtualFile.exists()) {
			URI uri = VFSUtils.getPhysicalURI(virtualFile);
			return uri.getPath();
		}

		return url.getPath();
	}

}
