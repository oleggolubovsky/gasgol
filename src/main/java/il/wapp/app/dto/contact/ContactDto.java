package il.wapp.app.dto.contact;

import lombok.*;

import java.io.*;
import java.util.*;

@Data
@Builder
public class ContactDto implements Serializable {

	private Long id;

	private String name;

	private String phone;

	private Date createdAt;

	private Date updatedAt;

}
