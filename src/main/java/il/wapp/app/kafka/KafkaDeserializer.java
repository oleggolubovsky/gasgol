package il.wapp.app.kafka;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import org.apache.kafka.common.serialization.*;

import java.io.*;
import java.util.*;

public class KafkaDeserializer<T> implements Deserializer<T> {

	private ObjectMapper mapper;

	@Override
	public void close() {
	}

	@Override
	public void configure(final Map<String, ?> settings, final boolean isKey) {
		mapper = new ObjectMapper();
	}

	@Override
	public T deserialize(final String topic, final byte[] bytes) {
		try {
			return mapper.readValue(bytes, new TypeReference<T>() {
			});
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
