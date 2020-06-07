package il.wapp.app.rest;

import il.wapp.app.dto.common.*;
import il.wapp.app.dto.contact.*;
import il.wapp.app.security.*;
import il.wapp.app.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/contactLists")
public class ContactListController {

	@Autowired
	private ContactListService contactListService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@RealUser
	public ContactListDto getById(@PathVariable("id") Long id) {
		return contactListService.findById(id).toDto();
	}

	@RequestMapping(method = RequestMethod.POST)
	@RealUser
	public ContactListDto save(@RequestBody RegisterContactListDto contactList) {
		return contactListService.create(contactList).toDto();
	}

	@RequestMapping(method = RequestMethod.PUT)
	@RealUser
	public ContactListDto edit(@RequestBody RegisterContactListDto contactList) {
		return contactListService.update(contactList).toDto();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@RealUser
	public void delete(@PathVariable("id") Long id) {
		contactListService.delete(id);
	}

	@GetMapping()
	@RealUser
	public Page<ContactListDto> findAll(
		@RequestParam(value = "page", defaultValue = "0") int pageIndex,
		@RequestParam(value = "size", defaultValue = "10") int pageSize,
		@RequestParam(value = "sort", defaultValue = "name") String sort,
		@RequestParam(value = "direction", defaultValue = "asc", required = false) String direction,
		@RequestParam(value = "contactListId", required = false) Long contactListId
	) {
		return contactListService.findAll(contactListId, PageRequest.of(pageIndex, pageSize,
			Sort.by("asc".equals(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sort))
		);
	}

	@GetMapping("/phone")
	@RealUser
	public ContactWithContactListDto findAllByPhone(
		@RequestParam(value = "phoneNumber") String phoneNumber,
			@RequestParam(value = "contactListId") Long contactListId
	) {
		return contactListService.findAllByPhone(phoneNumber, contactListId);
	}

	@GetMapping("/search")
	@RealUser
	public List<SearchDto> findByNameLike(
		@RequestParam(value = "search", defaultValue = "") String name,
		@RequestParam(value = "limit", defaultValue = "10") int limit
	) {
		return contactListService.findByNameLike(name, limit);
	}

}
