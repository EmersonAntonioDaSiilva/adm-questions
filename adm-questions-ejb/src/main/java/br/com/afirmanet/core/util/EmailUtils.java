package br.com.afirmanet.core.util;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.SneakyThrows;
import br.com.afirmanet.core.bean.Email;
import br.com.afirmanet.core.enumeration.EmailFormatEnum;
import br.com.afirmanet.core.exception.ApplicationException;

import com.google.common.collect.Lists;

public final class EmailUtils {

	public static void send(final Properties properties, final Email email) {
		send(properties, email.getTo(), email.getCc(), email.getSubject(), email.getMessage(), email.getFormat().getValue(), email.getAttachments());
	}

	@SneakyThrows(MessagingException.class)
	public static void send(final Properties properties, final String to, final List<String> cc, final String subject, final String message, final String format, final File... attachments) {
		if (StringUtils.isBlank(to) || StringUtils.isBlank(subject) || StringUtils.isBlank(message)) {
			throw new ApplicationException("Os parâmetros 'Para', 'Assunto' e 'Mensagem' são obrigatórios!");
		}

		// Configuração para envio de e-mail via SMTP
		String encoding = PropertiesUtils.getValue(properties, "mail.encoding");
		String content = String.format("%s; charset=%s", StringUtils.isBlank(format) ? EmailFormatEnum.TEXT_PLAIN.getValue() : format, encoding);
		
		String userName = PropertiesUtils.getValue(properties, "mail.from");
		String password = PropertiesUtils.getValue(properties, "mail.password");
		
		Session session = Session.getInstance(properties,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		  });

		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setHeader("Content-type", encoding);
		mimeMessage.setFrom(new InternetAddress(PropertiesUtils.getValue(properties, "mail.from")));
		mimeMessage.setSubject(subject, encoding);
		mimeMessage.setRecipients(Message.RecipientType.TO, convertEmailAddresses(Lists.newArrayList(to)));
		mimeMessage.setSentDate(new Date());

		if (cc != null && !cc.isEmpty()) {
			mimeMessage.setRecipients(Message.RecipientType.CC, convertEmailAddresses(cc));
		}

		Multipart multipart = new MimeMultipart();

		// Mensagem
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(message, content);
		multipart.addBodyPart(messageBodyPart);

		// Anexos
		if(attachments != null && attachments.length > 0){
			for (File attachment : attachments) {
				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
				DataSource dataSource = new FileDataSource(attachment);
				attachmentBodyPart.setDisposition(Part.ATTACHMENT);
				attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
				attachmentBodyPart.setFileName(dataSource.getName());
	
				multipart.addBodyPart(attachmentBodyPart);
			}
		}
		
		mimeMessage.setContent(multipart);
		mimeMessage.saveChanges();

		// Enviar/Tansportar mensagem
		Transport.send(mimeMessage);
	}

	@SneakyThrows(AddressException.class)
	private static InternetAddress[] convertEmailAddresses(List<String> emails) {
		InternetAddress[] addresses = new InternetAddress[emails.size()];
		for (int index = 0; index < emails.size(); index++) {
			addresses[index] = new InternetAddress(emails.get(index));
		}

		return addresses;
	}

}
