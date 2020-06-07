package il.wapp.app.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseMessageTemplateDto implements Serializable {

	private boolean resendTomorrow;
	private String mediaFile;
	private Long templateId;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TypeNumber {
		private String type;

		public String getType() {
			return "whatsapp";
		}

		private String number;
	}

	@Data
	@Builder
	public static class MessageTemplate {
		private Content content;
	}

	@Data
	@Builder
	public static class Content {
		private String type;
		private Custom custom;
	}

	@Data
	@Builder
	public static class Custom {
		private String type;
		private Template template;
	}

	@Data
	@Builder
	public static class Template {
		private String name;
		private String namespace;
		private Language language;
		private List<Component> components;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Component {
		private String type;
		private List<Parameter> parameters;
	}

	@Data
	@Builder
	public static class Language {
		private String code;
		private String policy;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Parameter {
		private String type;
		private String text;
		private boolean rtl;
	}

}
