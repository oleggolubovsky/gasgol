package il.wapp.app.service.impl;

import il.wapp.app.domain.*;
import il.wapp.app.dto.common.*;
import il.wapp.app.dto.contact.*;
import il.wapp.app.file.*;
import il.wapp.app.repository.*;
import il.wapp.app.service.*;
import il.wapp.app.validation.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactListRepository contactListRepository;

    @Autowired
    private ContactValidator contactValidator;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PermissionValidator permissionValidator;

    @Autowired
    private UserService userService;

    @Autowired
    private MinioService minioService;

    @Override
    public Contact findById(Long id, Long contactListId) {
        return permissionValidator.validateContact(contactListId, id);
    }

    @Override
    @Transactional
    public ContactDto create(ContactRegisterDto dto) {
        permissionValidator.validateContactList(dto.getContactListId());
        User user = userService.currentUser();
        Contact contact = contactValidator.validate(dto, userService.currentUser());
        contact.setUser(user);
        Set<Long> contactLists = dto.getContactListIds();
        if (contactLists != null && !contactLists.isEmpty()) {
            contact.setContactList(contactLists.stream()
                    .map(id -> permissionValidator.validateContactList(id))
                    .collect(Collectors.toList()));
//            contact.getContactList().add(contactListRepository.getOne(dto.getContactListId()));
        } else {
            contact.setContactList(new ArrayList<>());
        }
        return contactRepository.save(contact).toDto();
    }

    @Transactional
    public ContactDto createFromExcel(ContactRegisterDto dto) {
        permissionValidator.validateContactList(dto.getContactListId());
        User user = userService.currentUser();
        Contact contact = contactValidator.validate(dto, userService.currentUser());
        contact.setUser(user);
        return contactRepository.save(contact).toDto();
    }

    @Override
    @Transactional
    public Contact update(ContactRegisterDto dto) {
        permissionValidator.validateContact(dto.getContactListId(), dto.getId());
        Contact entity = contactValidator.validate(ContactRegisterDto.builder()
                .id(dto.getId())
                .phone(dto.getPhone())
                .name(dto.getName())
                .contactListId(dto.getContactListId())
                .build(), userService.currentUser());
        Set<Long> contactLists = dto.getContactListIds();
        contactLists.add(dto.getContactListId());
        if (contactLists != null && contactLists.size() > 0) {
            entity.setContactList(contactLists.stream()
                    .map(id -> permissionValidator.validateContactList(id))
                    .collect(Collectors.toList()));
        } else {
            entity.setContactList(new ArrayList<>());
        }
        return contactRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id, Long contactListId) {
        Contact contact = permissionValidator.validateContact(contactListId, id);
        if (contactListId == null || contactListId == 0) {
            contact.setContactList(new ArrayList<>());
        } else {
            contact.getContactList().remove(contactListRepository.findById(contactListId).get());
        }
        contactRepository.save(contact);
    }

    @Override
    public Page<ContactDto> findAll(Long contactListId, Long contactId, Pageable pageable) {
        if (contactListId != null && contactListId != 0) {
            permissionValidator.validateContactList(contactListId);
            ContactList contactList = contactListRepository.findById(contactListId).get();
            List<Contact> contacts = (contactId != null)
                    ? Arrays.asList(permissionValidator.validateContact(null, contactId))
                    : contactList.getContacts().stream().collect(collectingAndThen(
                    toCollection(() -> new TreeSet<>(comparingLong(Contact::getId))),
                    ArrayList::new
            ));
            return new PageImpl<>(contacts
                    .stream().map(c -> c.toDto()).collect(Collectors.toList())
                    , pageable, contacts.size());
        } else {
            Page<Contact> pageContacts = contactRepository
                    .findByCorporation(userService.currentUser().getCorporation(), pageable);
            List<Contact> contacts = pageContacts.getContent();
            return new PageImpl<>(contacts
                    .stream().collect(collectingAndThen(
                            toCollection(() -> new TreeSet<>(comparingLong(Contact::getId))),
                            ArrayList::new
                    ))
                    .stream().map(c -> c.toDto()).collect(Collectors.toList())
                    , pageable, contacts.size());
        }
    }

    @Override
    @Transactional
    public List<SearchDto> findByNamePhoneLike(String search, int limit, Long contactListId) {
        permissionValidator.validateContactList(contactListId);
        List<Object[]> result = entityManager.createQuery("select c.id, c.name from Contact c " +
                "where UPPER(c.name) like UPPER(:search) or UPPER(c.phone.number) like UPPER(:search) " +
                "and c.user.corporation=:corporation " +
                "order by c.name")
                .setParameter("search", "%" + search + "%")
                .setParameter("corporation", userService.currentUser().getCorporation())
                .setMaxResults(limit)
                .getResultList();
        return result.stream()
                .map(object -> new SearchDto((Long) object[0], (String) object[1]))
                .filter(c -> contactListRepository
                        .findById(contactListId).get().getContacts().contains(contactRepository.findById(c.getId()).get()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ContactRegisterDto> registerContacts(Long contactListId, boolean updateAll, String fileUrl) throws Exception {
        List<ContactRegisterDto> contacts = FileReader.registerContacts(fileUrl, minioService.getFile(fileUrl));
        ContactList contactList = permissionValidator.validateContactList(contactListId);
        if (updateAll) {
            contactListRepository.deleteByContactListId(contactList.getId());
        }
        contacts.stream()
                .filter(distinctByKey(p -> p.getPhone()))
                .forEach(contact ->
                        createFromExcel(ContactRegisterDto.builder()
                                .phone(contact.getPhone())
                                .name(contact.getName())
                                .contactListId(contactListId)
                                .build())
                );
        return contacts;
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
