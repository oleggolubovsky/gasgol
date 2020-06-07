package il.wapp.app.dto.contact;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ContactWithContactListDto implements Serializable {

	private Long contactId;

	private String contactName;

	private List<ContactListDto> contactList;

}
