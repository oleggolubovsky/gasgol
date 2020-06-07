package il.wapp.app.kafka;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import org.apache.kafka.common.serialization.*;

import java.util.*;

public class KafkaSerializer<T> implements Serializer<T> {

	private ObjectMapper mapper;

	@Override
	public void close() {
	}

	@Override
	public void configure(final Map<String, ?> settings, final boolean isKey) {
		mapper = new ObjectMapper();
	}

	@Override
	public byte[] serialize(final String topic, final T object) {
		try {
			return mapper.writeValueAsBytes(object);
		} catch (final JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
