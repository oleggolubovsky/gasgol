package il.wapp;

import il.wapp.app.domain.*;
import il.wapp.app.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class WappBackendApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ContactListRepository contactListRepository;

	public User createUser(Corporation corporation, String userEmail) {
		User user = new User();
		user.setCorporation(corporation);
		user.setPassword("test");
		user.setEmail(userEmail);
		//corporation.setUsers(Arrays.asList(user));
		userRepository.save(user);
		assertNotNull(user);
		assertEquals(userEmail, user.getEmail());
		return user;
	}

	public ContactList createContactList(String corporationName, String userEmail) {
		Corporation corporation = new Corporation(corporationName, new Date());
		ContactList contactList = new ContactList();
		contactList.setName("new");
		contactList.setUser(createUser(corporation, userEmail));
		contactListRepository.save(contactList);
		return contactList;
	}

	public ContactList createContactList(Corporation corporation, String userEmail) {
		ContactList contactList = new ContactList();
		contactList.setName("new");
		contactList.setUser(createUser(corporation, userEmail));
		contactListRepository.save(contactList);
		return contactList;
	}
}
