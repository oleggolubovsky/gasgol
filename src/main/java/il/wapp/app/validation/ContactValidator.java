package il.wapp.app.validation;

import com.fasterxml.jackson.databind.*;
import il.wapp.app.domain.*;
import il.wapp.app.dto.contact.*;
import il.wapp.app.exception.*;
import il.wapp.app.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class ContactValidator extends BaseEntityValidator {

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private ContactListRepository contactListRepository;

	@Autowired
	private PhoneRepository phoneRepository;

	@Autowired
	private ObjectMapper jacksonObjectMapper;

	public Contact validate(ContactRegisterDto dto, User user) {

		List<AttrValidationError> errors = new ArrayList<>();
		Contact contact = new Contact();
        String phoneNumber = checkPhoneFormat(dto.getPhone(), errors);
		if (checkRequired(dto.getPhone(), "phone", errors) && checkRequired(phoneNumber, "phone", errors)
			&& checkRequired(dto.getContactListId(), "contactListId", errors)) {
			Optional<Phone> phone = phoneRepository.findById(phoneNumber);
			Optional<ContactList> contactList = contactListRepository.findById(dto.getContactListId());
			if (!contactList.isPresent()) {
				throw new AttrValidationErrorException("ContactListId with id = " + dto.getContactListId() + " not exist");
			}
			Date currentDate = new Date();
			if (!phone.isPresent()) {
				contact =
					(dto.getId() != null && dto.getId() > 0 && contactRepository.findById(dto.getId()).isPresent())
					? contactRepository.findById(dto.getId()).get()
					: new Contact();
				contact.setName(dto.getName());
				contact.setPhone(new Phone(phoneNumber));
				contact.setContactList(new ArrayList<>(Arrays.asList(contactList.get())));
			} else {
				contact = contactRepository.findByPhoneAndCorporation(phoneNumber, user.getCorporation());
				if (contact == null) {
					contact = new Contact();
					contact.setCreatedAt(currentDate);
					contact.setPhone(phone.get());
					contact.setContactList(new ArrayList<>(Arrays.asList(contactList.get())));
				} else if (!contactList.get().getContacts().contains(contact)) {
					contact.getContactList().add(contactList.get());
				}
				contact.setUpdatedAt(currentDate);
				contact.setCreatedAt(currentDate);
				contact.setName(dto.getName());
			}
		}
		parseErrors(errors, jacksonObjectMapper);
		return contact;
	}

	public static String checkPhoneFormat(String phone, List<AttrValidationError> errors) {
		phone = phone.replaceAll("[^0-9]", "");
		if (phone.startsWith("0") && phone.length() == 10) {
			return "972" + phone.substring(1, 9);
		} else if (phone.startsWith("9725") && phone.length() == 12) {
			return phone;
		}
		errors.add(new AttrValidationError("phone", "Incorrect phone format for: " + phone));
		return "";
	}

}
