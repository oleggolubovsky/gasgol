//package il.wapp;
//
//import il.wapp.app.domain.*;
//import il.wapp.app.dto.contact.*;
//import il.wapp.app.repository.*;
//import il.wapp.app.service.*;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.*;
//import org.springframework.boot.test.context.*;
//import org.springframework.transaction.annotation.*;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@SpringBootTest
//class ContactTests extends WappBackendApplicationTests {
//
//	@Autowired
//	ContactService contactService;
//
//	@Autowired
//	ContactRepository contactRepository;
//
//	@Autowired
//	UserRepository userRepository;
//
//	@Autowired
//	ContactListRepository contactListRepository;
//
//
//	@Test
//	@Transactional
//	void checkCreateContactForDifferentCorporation() {
//
//		ContactList contactList1 = createContactList("corp1", "test1@test.ru");
//		ContactList contactList2 = createContactList("corp2", "test2@test.ru");
//
//		contactService.create(ContactRegisterDto.builder()
//			//.name("new")
//			.phone("972647383894")
//			.contactListId(contactList1.getId()).build());
//		contactService.create(ContactRegisterDto.builder()
//			//.name("new_name")
//			.phone("972647383892")
//			.contactListId(contactList2.getId()).build());
//		assertEquals("corp1", contactList1.getUser().getCorporation().getName());
//		assertEquals("corp2", contactList2.getUser().getCorporation().getName());
//	}
//
//	@Test
//	@Transactional
//	void checkCreateContactsInSameCorporation() {
//		Corporation corporation = new Corporation("corp1", new Date());
//
//		ContactList contactList1 = createContactList(corporation, "test3@test.ru");
//		ContactList contactList2 = createContactList(corporation, "test2@test.ru");
//		contactService.create(ContactRegisterDto.builder()
//			.name("new")
//			.phone("97264dfhdf73838dhdfh--94dfhdfh")
//			.contactListId(contactList1.getId()).build());
//		contactService.create(ContactRegisterDto.builder()
//			.name("new_name")
//			.phone("0647383894")
//			.contactListId(contactList2.getId()).build());
//		assertEquals("corp1", contactList1.getUser().getCorporation().getName());
//		assertEquals("corp1", contactList2.getUser().getCorporation().getName());
//	}
//
//}
