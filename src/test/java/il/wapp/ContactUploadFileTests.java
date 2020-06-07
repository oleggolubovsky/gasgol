//package il.wapp;
//
//import il.wapp.app.domain.*;
//import il.wapp.app.service.*;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.*;
//import org.springframework.transaction.annotation.*;
//
//import java.io.*;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//public class ContactUploadFileTests extends WappBackendApplicationTests {
//	@Autowired
//	ContactService contactService;
//
//	@Test
//	@Transactional
//	void uploadRegisterFile() throws Exception {
//		String path = "src/test/resources/files/reg.xls";
//
//		File file = new File(path);
//		String absolutePath = file.getAbsolutePath();
//		assertEquals(contactService.registerContacts(
//				createContactList(new Corporation("test", new Date()), "").getId()
//				, true,
//				"file:///"+absolutePath
//			).size(), 4);
//	}
//}
