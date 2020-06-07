package il.wapp.app.domain;

import lombok.*;

import javax.persistence.*;
import java.io.*;
import java.util.*;

@Data
@Entity
@Table(name = "phones")
@NoArgsConstructor
public class Phone implements Serializable {

	public Phone(String number) {
		this.number = number;
	}

	@Id
	private String number;

	@OneToMany(mappedBy = "phone")
	private List<Contact> contacts;

}
