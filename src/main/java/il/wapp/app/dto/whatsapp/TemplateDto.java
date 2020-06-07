package il.wapp.app.dto.whatsapp;

import il.wapp.app.dto.campaign.WhatsAppMessageTemplateDto;
import lombok.*;

import java.io.*;
import java.util.*;

@Data
@Builder
public class TemplateDto implements Serializable {

	private Long id;

	private String name;

	private String status;

	private Date createdAt;

	private WhatsAppTemplateDto jsonContent;

}
