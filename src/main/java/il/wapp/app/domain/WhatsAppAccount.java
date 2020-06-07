package il.wapp.app.domain;

import lombok.*;

import javax.persistence.*;
import java.io.*;

@Data
@Entity
@Table(name="whatsapp_accounts")
@NoArgsConstructor
public class WhatsAppAccount implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "message_api_url")
	private String messageApiUrl;

	@Column(name = "app_number")
	private String appNumber;

	@Column(name = "app_template_namespace")
	private String appTemplateNamespace;

}
