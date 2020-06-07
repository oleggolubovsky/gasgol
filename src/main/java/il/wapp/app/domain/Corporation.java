package il.wapp.app.domain;

import lombok.*;

import javax.persistence.*;
import java.io.*;
import java.util.*;

@Data
@Entity
@Table(name="corporations")
@NoArgsConstructor
public class Corporation implements Serializable {

	public Corporation(String name,  Date createAt) {
		this.name = name;
		this.createAt = createAt;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private Date createAt;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="whatsapp_account_id")
	private WhatsAppAccount whatsAppAccount;

}
