package il.wapp.app.kafka;

import il.wapp.app.dto.campaign.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.kafka.core.*;
import org.springframework.stereotype.*;

@Service
@Slf4j
public class Producer {

	@Value("${kafka.topic.nexmo.prepare_template_uri}")
	private String nexmoTemplateTopic;

	@Value("${kafka.topic.nexmo.prepare_template_uri}")
	private String nexmoTemplateTopicStatus;

	@Value("${kafka.topic.nexmo.whatsapp_message_uri}")
	private String nexmoWhatsAppMessage;

	@Autowired
	private KafkaTemplate<String, SendCampaignMessageDto> kafkaTemplate;

	@Autowired
	private KafkaTemplate<String, CampaignContactDto> kafkaCampaignContact;

	public void sendWhatsAppMessage(CampaignContactDto message) {
		log.info(String.format(nexmoWhatsAppMessage + " -> Producing message -> %s", message));
		kafkaCampaignContact.send(nexmoWhatsAppMessage, message);
	}

	public void sendCampaign(SendCampaignMessageDto message) {
		log.info(String.format(nexmoTemplateTopic + "-> Producing message -> %s", message));
		kafkaTemplate.send(nexmoTemplateTopic, message);
	}

}
