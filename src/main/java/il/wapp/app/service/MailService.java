package il.wapp.app.service;

import org.springframework.stereotype.*;

@Repository
public interface MailService {

	void sendRegisterMail(String toEmail, String link, String topic, String text);

}
