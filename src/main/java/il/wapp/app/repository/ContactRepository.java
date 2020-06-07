package il.wapp.app.repository;

import il.wapp.app.domain.Contact;
import il.wapp.app.domain.Corporation;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query(value = "SELECT c FROM Contact c " +
            "where c.phone.number=:phoneNumber " +
            "and c.user.corporation=:corporation")
    Contact findByPhoneAndCorporation(@Param("phoneNumber") String phoneNumber,
                                      @Param("corporation") Corporation corporation);

    @Query(value = "SELECT c FROM Contact c " +
        "join c.contactList cl \n" +
        "where c.phone.number=:phoneNumber " +
        "and cl.user.corporation=:corporation")
    Contact findByPhoneAndCorporationInContactList(@Param("phoneNumber") String phoneNumber,
        @Param("corporation") Corporation corporation);

    @Query(value = "SELECT c FROM Contact c " +
            "join c.contactList cl \n" +
            "where cl.user.corporation= :corporation")
    Page<Contact> findByCorporation(@Param("corporation") Corporation corporation, Pageable pageable);

    @Query(value = "SELECT c FROM Contact c " +
        "join c.contactList cl \n" +
        "where cl.user.corporation= :corporation")
    List<Contact> findByCorporation(@Param("corporation") Corporation corporation);

    @Modifying
    @Query(value = "DELETE from contact_to_contact_list " +
            "where contact_id= :contactId", nativeQuery = true)
    int deleteByContactId(@Param("contactId") Long contactId);

}
