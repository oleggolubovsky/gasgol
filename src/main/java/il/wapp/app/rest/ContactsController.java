package il.wapp.app.rest;

import il.wapp.app.dto.common.*;
import il.wapp.app.dto.contact.*;
import il.wapp.app.security.*;
import il.wapp.app.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/contacts")
public class ContactsController {

	@Autowired
	private ContactService contactService;

	@Autowired
	private MinioService minioService;

	@RequestMapping(value = "/{id}/{contactListId}", method = RequestMethod.GET)
	@RealUser
	public ContactDto getById(@PathVariable("id") Long id, @PathVariable("contactListId") Long contactListId) {
		return contactService.findById(id, contactListId).toDto();
	}

	@RequestMapping(method = RequestMethod.POST)
	@RealUser
	public ContactDto save(@RequestBody ContactRegisterDto dto) {
		return contactService.create(dto);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@RealUser
	public ContactDto edit(@RequestBody ContactRegisterDto contact) {
		return contactService.update(contact).toDto();
	}

	@RequestMapping(value = "/{id}/{contactListId}", method = RequestMethod.DELETE)
	@RealUser
	public void delete(@PathVariable("id") Long id, @PathVariable("contactListId") Long contactListId) {
		contactService.delete(id, contactListId);
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@RealUser
	public Page<ContactDto> findAll(
		@RequestParam(value = "contactListId", required = false) Long contactListId,
		@RequestParam(value = "contactId", required = false) Long contactId,
		@RequestParam(value = "page", defaultValue = "0") int pageIndex,
		@RequestParam(value = "size", defaultValue = "10") int pageSize, @RequestParam(value = "sort", defaultValue = "name") String sort,
		@RequestParam(value = "direction", defaultValue = "asc", required = false) String direction
	) {
		return contactService.findAll(
			contactListId, contactId,
			PageRequest.of(pageIndex, pageSize,
				Sort.by("asc".equals(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sort)
			)
		);
	}

	@GetMapping("/search")
	@RealUser
	public List<SearchDto> findByNamePhoneLike(
		@RequestParam(value = "search", defaultValue = "") String search
		, @RequestParam(value = "limit", defaultValue = "10") int limit,
		@RequestParam(value = "contactListId") Long contactListId
	) {
		return contactService.findByNamePhoneLike(search, limit, contactListId);
	}

	@PostMapping("/regUploadFile")
	@RealUser
	public List<ContactRegisterDto> handleFileUpload(
		@RequestParam("contactListId") Long contactListId,
		@RequestParam("updateAll") boolean updateAll, @RequestParam("link") String link
	) throws Exception {
		return contactService.registerContacts(contactListId, updateAll, link);
	}

	@PostMapping(path = "/upload")
	@RealUser
	public String uploadFile(@RequestPart(value = "file") MultipartFile files) throws Exception {
		return minioService.uploadFile(files.getOriginalFilename(), files.getBytes());
	}
}
