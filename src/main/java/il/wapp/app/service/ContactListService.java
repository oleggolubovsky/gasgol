package il.wapp.app.service;

import il.wapp.app.domain.*;
import il.wapp.app.dto.common.*;
import il.wapp.app.dto.contact.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ContactListService {

    String FIND_CONTACT_LIST_BY_PHONE_SELECT = "select \"id\", \"name\", count(\"id\")-1 as \"enable\", \"updated_at\"\n" +
            "from\n" +
            "(SELECT cl.id as \"id\", cl.name as \"name\", cl.updated_at as \"updated_at\", true as \"enable\" from contact_lists cl, contacts cont\n" +
            ", contact_to_contact_list ctcl\n" +
            "where \n" +
            "cl.id = ctcl.contact_list_id and \n" +
            "cont.id = ctcl.contact_id and \n" +
            "cont.phone_number = :phoneNumber and \n" +
            "cl.user_id in (select id from users where corporation_id =:corporationId) \n" +
            "union all\n" +
            "\n" +
            "SELECT cl.id as \"id\", cl.name as \"name\", cl.updated_at as \"updated_at\", false as \"enable\" from contact_lists cl\n" +
            "where \n" +
            "cl.user_id in (select id from users where corporation_id =:corporationId) ) t\n" +
            "group by \"id\", \"name\", \"updated_at\"\n" +
            "order by \"updated_at\" desc";

    ContactList findById(Long id);

    ContactList create(RegisterContactListDto dto);

    ContactList update(RegisterContactListDto dto);

    void delete(Long id);

    Page<ContactListDto> findAll(Long contactListId, Pageable pageable);

    List<SearchDto> findByNameLike(String name, int limit);

    ContactWithContactListDto findAllByPhone(String phoneNumber, Long contactListId);
}
