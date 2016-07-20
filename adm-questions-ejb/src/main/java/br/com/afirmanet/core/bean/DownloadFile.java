package br.com.afirmanet.core.bean;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

import org.apache.commons.io.FilenameUtils;

@Data
@ToString(exclude = { "fileContent" })
public class DownloadFile implements Serializable {

	private static final long serialVersionUID = 3608327131536094456L;

	private String fileName;
	private byte[] fileContent;

	public String getContentType() {
		return FilenameUtils.getExtension(fileName);
	}

}
