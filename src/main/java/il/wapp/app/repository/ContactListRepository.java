package il.wapp.app.repository;

import il.wapp.app.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;


public interface ContactListRepository extends JpaRepository<ContactList, Long> {

	@Query(value = "SELECT cl FROM ContactList cl " +
			"where cl.user.corporation= :corporation")
	List<ContactList> findAllByCorporation(Corporation corporation, Pageable pageable);

	@Query(value = "SELECT cl FROM ContactList cl " +
			"where cl.user.corporation= :corporation")
	List<ContactList> findAllByCorporation(Corporation corporation);

	@Modifying
	@Query(value = "DELETE from contact_to_contact_list " +
		"where contact_list_id= :contactListId", nativeQuery = true)
	int deleteByContactListId(@Param("contactListId") Long contactListId);

}
