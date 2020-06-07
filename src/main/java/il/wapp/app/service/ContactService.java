package il.wapp.app.service;

import il.wapp.app.domain.*;
import il.wapp.app.dto.common.*;
import il.wapp.app.dto.contact.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ContactService{

	Contact  findById (Long id, Long contactListId);

	ContactDto create (ContactRegisterDto dto);

	Contact update (ContactRegisterDto dto);

	void delete(Long id, Long contactListId);

	Page<ContactDto> findAll(Long contactListId, Long contactId, Pageable pageable);

	List<SearchDto> findByNamePhoneLike(String search, int limit, Long contactListId);

	List<ContactRegisterDto> registerContacts(Long contactListId, boolean updateAll, String link) throws Exception;

}
