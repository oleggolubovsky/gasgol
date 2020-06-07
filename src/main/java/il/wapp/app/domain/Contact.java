package il.wapp.app.domain;

import il.wapp.app.dto.contact.*;
import lombok.*;

import javax.persistence.*;
import java.io.*;
import java.util.*;

@Data
@Entity
@Table(name = "contacts")
public class Contact implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@Column(name = "created_at", updatable = false)
	private Date createdAt = new Date();

	@Column(name = "updated_at")
	private Date updatedAt = new Date();

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "phone_number", nullable = false)
	private Phone phone;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="user_id", nullable=false)
	private User user;

	@ManyToMany
	@JoinTable(
		name = "contact_to_contact_list",
		joinColumns = {@JoinColumn(name = "contact_id")},
		inverseJoinColumns = {@JoinColumn(name = "contact_list_id")}
	)
	private List<ContactList> contactList = new ArrayList<>();

	public ContactDto toDto() {
		return ContactDto.builder()
			.id(id)
			.name(name)
			.phone(phone.getNumber())
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}

}
