package il.wapp.app.domain;

import il.wapp.app.dto.contact.*;
import lombok.*;

import javax.persistence.*;
import java.io.*;
import java.util.*;

@Data
@Entity
@Table(name="contact_lists")
public class ContactList implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String name;

	@Column(name = "created_at", updatable = false)
	private Date createdAt = new Date();

	@Column(name = "updated_at")
	private Date updatedAt = new Date();

	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;

	@ManyToMany(mappedBy = "contactList")
	private List<Contact> contacts = new ArrayList<>();

	public ContactListDto toDto(){
		return ContactListDto.builder()
			.id(id)
			.name(name)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			//.contacts(contacts.stream().map(contact -> contact.toDto()).collect(Collectors.toList()))
			.build();
	}
}
