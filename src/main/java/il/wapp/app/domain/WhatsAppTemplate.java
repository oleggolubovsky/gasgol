package il.wapp.app.domain;

import com.fasterxml.jackson.databind.*;
import com.vladmihalcea.hibernate.type.json.*;
import il.wapp.app.dto.campaign.WhatsAppMessageTemplateDto;
import il.wapp.app.dto.whatsapp.*;
import il.wapp.app.enums.*;
import il.wapp.app.exception.*;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.*;
import java.util.*;

@Data
@Entity
@Table(name = "whatsapp_templates")
@NoArgsConstructor
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
public class WhatsAppTemplate implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@Column(name = "created_at")
	private Date createdAt;

	@Type(type = "json")
	@Column(name = "json_content", columnDefinition = "json")
	private String jsonContent;

	private WhatsAppTemplateStatus status;

	@ManyToOne
	@JoinColumn(name = "corporation_id")
	private Corporation corporation;

	public TemplateDto toDto() {
			return TemplateDto.builder()
				.createdAt(createdAt)
				.id(id)
				.status(status.name())
				.name(name)
				.build();
	}

	public TemplateDto toDto(ObjectMapper om) {
		try {
			return TemplateDto.builder()
				.createdAt(createdAt)
				.id(id)
				.status(status.name())
				.name(name)
				.jsonContent(om
					.readValue(jsonContent, WhatsAppTemplateDto.class))
				.build();
		} catch (Exception ex) {
			throw new ParseJsonException("Error parsing for template id = " + id);
		}
	}

}
