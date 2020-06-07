package il.wapp.app.dto.contact;

import lombok.*;

import java.io.*;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactRegisterDto implements Serializable {

	private Long id;

	private String name;

	private String phone;

	private Long contactListId;

	private Set<Long> contactListIds;
}
