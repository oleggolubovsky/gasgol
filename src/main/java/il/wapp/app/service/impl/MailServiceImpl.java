package il.wapp.app.service.impl;

import il.wapp.app.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

@Service
public class MailServiceImpl implements MailService {

	@Value("${register.from.mail}")
	private String fromMail;

	@Value("${register.mail.password}")
	private String password;

	@Value("${mail.smtp.host}")
	private String host;

	@Value("${mail.smtp.port}")
	private String port;

	@Override
	public void sendRegisterMail(String toEmail, String link, String topic, String text) {
		Properties props = new Properties();
		props.put("mail.smtp.host", host); //SMTP Host
		props.put("mail.smtp.port", port); //TLS Port
		props.put("mail.smtp.auth", "true"); //enable authentication
		props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromMail, password);
			}
		};
		Session session = Session.getInstance(props, auth);

		sendEmail(session, toEmail,topic, text + link);
	}

	private void sendEmail(Session session, String toEmail, String subject, String body){
		try
		{
			MimeMessage msg = new MimeMessage(session);
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			msg.setContent(body, "text/HTML");
			msg.setFrom(new InternetAddress(fromMail, subject));
			msg.setReplyTo(InternetAddress.parse(fromMail, false));
			msg.setSubject(subject, "UTF-8");
			msg.setText(body, "UTF-8");
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			Transport.send(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
