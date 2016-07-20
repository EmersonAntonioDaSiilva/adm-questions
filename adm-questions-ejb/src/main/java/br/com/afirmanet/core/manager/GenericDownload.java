package br.com.afirmanet.core.manager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.event.ActionEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.afirmanet.core.bean.DownloadFile;
import br.com.afirmanet.core.enumeration.ContentTypeEnum;
import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.core.util.ApplicationPropertiesUtils;

import com.google.common.io.Files;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class GenericDownload<T, ID extends Serializable, D extends GenericDAO<?, ?>> extends GenericSearch<T, ID, D> {

	private boolean showDownloadButton = ApplicationPropertiesUtils.getValueAsBoolean("showDownloadButton", "true");

	private StreamedContent streamedContent;

	@SuppressWarnings("unchecked")
	public void downloadFile(ActionEvent actionEvent) {
		try {
			T entity = (T) actionEvent.getComponent().getAttributes().get("entity");

			beforeDownloadFile(entity);

			DownloadFile downloadFile = createDownloadFile(entity);
			streamedContent = prepareFileForDownload(downloadFile);

			afterDownloadFile(entity, downloadFile);

		} catch (ApplicationException e) {
			addErrorMessage(e);
		} catch (Throwable t) {
			addFatalErrorMessage(t);
		}
	}

	protected abstract DownloadFile createDownloadFile(T entity);

	/**
	 * @param entity
	 */
	protected void beforeDownloadFile(T entity) {
		// Basta implementar o que vc precisa
	}

	/**
	 * @param entity
	 * @param downloadFile
	 */
	protected void afterDownloadFile(T entity, DownloadFile downloadFile) {
		// Basta implementar o que vc precisa
	}

	protected StreamedContent prepareFileForDownload(DownloadFile downloadFile) {
		String fileExtension = Files.getFileExtension(downloadFile.getFileName());
		ContentTypeEnum contentType = ContentTypeEnum.valueOf(fileExtension.toUpperCase());

		InputStream inputStream = new ByteArrayInputStream(downloadFile.getFileContent());
		return new DefaultStreamedContent(inputStream, contentType.getValue(), downloadFile.getFileName());
	}

}
