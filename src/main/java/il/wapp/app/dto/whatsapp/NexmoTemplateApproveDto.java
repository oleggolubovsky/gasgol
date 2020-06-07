package il.wapp.app.dto.whatsapp;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Data
@Builder
public class NexmoTemplateApproveDto implements Serializable {

	private Long templateId;

	private Account from;

	private Account to;

	private Message message;

	@Data
	@Builder
	static class Message {
		private Content content;
	}

	@Data
	@Builder
	static class Content {
		private String type;
		private Template template;
	}

	@Data
	@Builder
	static class Template {
		private String name;
		private List<Parameter> parameters;
	}

	@Data
	static class Parameter {
		private String name;

		@JsonProperty("default")
		public String getName() {
			return name;
		}
	}

	@Data
	@Builder
	static class Account {
		private String type;
		private String number;
	}

}
