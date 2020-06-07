package il.wapp.app.configuration;

import il.wapp.app.dto.campaign.*;
import il.wapp.app.kafka.*;
import org.apache.kafka.clients.consumer.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.config.*;
import org.springframework.kafka.core.*;

import java.util.*;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	@Value(value = "${kafka.bootstrap}")
	private String bootstrapAddress;

	@Bean
	public ConsumerFactory<String, CampaignContactDto> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(
			ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
			bootstrapAddress);
		props.put(
			ConsumerConfig.GROUP_ID_CONFIG,
			"groupId");
		props.put(
			ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
			KafkaDeserializer.class);
		props.put(
			ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
			KafkaDeserializer.class);
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, CampaignContactDto>
	kafkaListenerContainerFactory() {

		ConcurrentKafkaListenerContainerFactory<String, CampaignContactDto> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}
