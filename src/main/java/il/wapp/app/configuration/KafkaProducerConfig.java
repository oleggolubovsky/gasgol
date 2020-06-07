package il.wapp.app.configuration;

import il.wapp.app.dto.campaign.*;
import il.wapp.app.kafka.*;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;

import java.util.*;

@Configuration
public class KafkaProducerConfig {

	@Value(value = "${kafka.bootstrap}")
	private String bootstrapAddress;

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(
			ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
			bootstrapAddress);
		configProps.put(
			ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
			KafkaSerializer.class);
		configProps.put(
			ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			KafkaSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, SendCampaignMessageDto> kafkaTemplate() {
		return new KafkaTemplate(producerFactory());
	}

	@Bean
	public KafkaTemplate<String, CampaignContactDto> kafkaCampaignContact() {
		return new KafkaTemplate(producerFactory());
	}

}
