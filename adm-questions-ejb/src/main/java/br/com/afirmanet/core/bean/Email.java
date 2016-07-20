package br.com.afirmanet.core.bean;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import br.com.afirmanet.core.enumeration.EmailFormatEnum;

@Data
@AllArgsConstructor
public class Email implements Serializable {

	private static final long serialVersionUID = -3232757292038299500L;

	private String to;
	private List<String> cc;
	private String subject;
	private String message;
	private EmailFormatEnum format;
	private File[] attachments;

	public Email() {
		format = EmailFormatEnum.TEXT_HTML;
	}

	public Email(String to, String subject, String message) {
		this.to = to;
		this.subject = subject;
		this.message = message;
		format = EmailFormatEnum.TEXT_HTML;
	}

	public Email(List<String> to, String subject, String message) {
		this.to = to.get(0);
		cc = to.subList(1, to.size());
		this.subject = subject;
		this.message = message;
		format = EmailFormatEnum.TEXT_HTML;
	}

	public Email(String to, List<String> cc, String subject, String message) {
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.message = message;
		format = EmailFormatEnum.TEXT_HTML;
	}

	public Email(List<String> to, String subject, String message, File[] attachments) {
		this.to = to.get(0);
		cc = to.subList(1, to.size());
		this.subject = subject;
		this.message = message;
		format = EmailFormatEnum.TEXT_HTML;
		this.attachments = attachments;
	}

	public Email(String to, List<String> cc, String subject, String message, File[] attachments) {
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.message = message;
		format = EmailFormatEnum.TEXT_HTML;
		this.attachments = attachments;
	}

	public Email(String to, List<String> cc, String subject, String message, EmailFormatEnum format) {
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.message = message;
		this.format = format;
	}

	public Email(List<String> to, String subject, String message, EmailFormatEnum format) {
		this.to = to.get(0);
		cc = to.subList(1, to.size());
		this.subject = subject;
		this.message = message;
		this.format = format;
	}

	public Email(List<String> to, String subject, String message, EmailFormatEnum format, File[] attachments) {
		this.to = to.get(0);
		cc = to.subList(1, to.size());
		this.subject = subject;
		this.message = message;
		this.format = format;
		this.attachments = attachments;
	}

}
