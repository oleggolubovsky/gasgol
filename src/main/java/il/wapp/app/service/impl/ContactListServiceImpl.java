package il.wapp.app.service.impl;

import il.wapp.app.constants.WappConstants;
import il.wapp.app.domain.*;
import il.wapp.app.dto.common.*;
import il.wapp.app.dto.contact.*;
import il.wapp.app.repository.*;
import il.wapp.app.service.*;
import il.wapp.app.validation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import javax.persistence.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;

@Service
public class ContactListServiceImpl implements ContactListService {

    @Autowired
    private ContactListRepository contactListRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionValidator permissionValidator;


    @Override
    public ContactList findById(Long id) {
        if (id == 0) {
            User user = userService.currentUser();
            List<Contact> contacts = getLastContact(user.getCorporation());
            ContactList contactList = new ContactList();
            contactList.setName(WappConstants.ALL_CONTACTS);
            contactList.setUpdatedAt(contacts.size() == 0
                    ? user.getCorporation().getCreateAt()
                    : contacts.get(0).getUpdatedAt());
            contactList.setCreatedAt(user.getCreatedAt());
            return contactList;
        }
        return permissionValidator.validateContactList(id);
    }

    @Override
    public ContactList create(RegisterContactListDto dto) {
        ContactList contactList = new ContactList();
        contactList.setCreatedAt(new Date());
        contactList.setUser(userService.currentUser());
        contactList.setName(dto.getName());
        return contactListRepository.save(contactList);
    }

    @Override
    public ContactList update(RegisterContactListDto dto) {
        ContactList entity = permissionValidator.validateContactList(dto.getId());
        entity.setName(dto.getName());
        return contactListRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == 0L) {
            User user = userService.currentUser();
            List<ContactList> contactLists = contactListRepository.findAllByCorporation(user.getCorporation());
            contactLists.forEach(cl -> {
                contactListRepository.deleteByContactListId(cl.getId());
                contactListRepository.delete(cl);
            });
        } else if (id > 0L) {
            ContactList contactList = permissionValidator.validateContactList(id);
            contactListRepository.deleteByContactListId(id);
            contactListRepository.delete(contactList);
        }
    }

    @Override
    @Transactional
    public Page<ContactListDto> findAll(Long contactListId, Pageable pageable) {
        User user = userService.currentUser();
        if (contactListId != null && contactListId > 0) {
            ContactList contactList = permissionValidator.validateContactList(contactListId);
            return new PageImpl(Collections.singletonList(contactList.toDto()), pageable,
                    contactListRepository.findAllByCorporation(user.getCorporation()).size());
        } else {
            List<ContactListDto> contactLists = contactListRepository
                    .findAllByCorporation(user.getCorporation(), pageable)
                    .stream().map(cl -> cl.toDto()).collect(Collectors.toList());
            List<Contact> contacts = getLastContact(user.getCorporation());
            ContactListDto allContacts = ContactListDto.builder()
                    .createdAt(user.getCorporation().getCreateAt())
                    .updatedAt(contacts.isEmpty()
                            ? user.getCorporation().getCreateAt()
                            : contacts.get(0).getUpdatedAt())
                    .name(WappConstants.ALL_CONTACTS)
                    .build();
            contactLists.add(0, allContacts);
            return new PageImpl(contactLists, pageable,
                    contactListRepository.findAllByCorporation(user.getCorporation()).size());
        }
    }

    @Override
    @Transactional
    public ContactWithContactListDto findAllByPhone(String phoneNumber, Long contactListId) {
        User user = userService.currentUser();
        List<Object[]> result = entityManager.createNativeQuery(FIND_CONTACT_LIST_BY_PHONE_SELECT)
                .setParameter("corporationId", user.getCorporation().getId())
                .setParameter("phoneNumber", phoneNumber)
                .getResultList();
        ContactWithContactListDto dto = ContactWithContactListDto.builder()
                .contactList(result.stream()
                    .filter(object -> !contactListId.equals(((BigInteger) object[0]).longValue()))
                    .map(object -> ContactListDto.builder()
                        .id(((BigInteger) object[0]).longValue())
                        .name((String) object[1])
                        .enable(((BigInteger) object[2]).longValue() > 0)
                        .updatedAt((Date) object[3])
                        .build())
                        .collect(Collectors.toList()))
                .build();
        ContactList contactList = findById(contactListId);
        ContactListDto contactListDto = contactList.toDto();
        Contact contact = contactRepository.findByPhoneAndCorporationInContactList(phoneNumber, user.getCorporation());
        if(contact != null){
            dto.setContactId(contact.getId());
            dto.setContactName(contact.getName());
        }
        if (contactList.getContacts().contains(contact)){
            contactListDto.setEnable(true);
        }
        dto.getContactList().add(0, contactListDto);
        return dto;
    }

    @Override
    public List<SearchDto> findByNameLike(String name, int limit) {
        List<Object[]> result = entityManager
            .createQuery("select cl.id, cl.name from ContactList cl " +
                "where UPPER(cl.name) like UPPER(:name) and cl.user.corporation=:corporation order by cl.name")
            .setParameter("name", "%" + name + "%")
            .setParameter("corporation", userService.currentUser().getCorporation())
            .setMaxResults(limit)
            .getResultList();
        List<SearchDto> searches = result.stream()
            .map(object -> new SearchDto((Long) object[0], (String) object[1]))
            .collect(Collectors.toList());
        if (WappConstants.ALL_CONTACTS.toLowerCase().contains(name.toLowerCase())) {
            searches.add(new SearchDto(0L, WappConstants.ALL_CONTACTS));
        }
        return searches;
    }

    private List<Contact> getLastContact(Corporation corporation) {
        return contactRepository.findByCorporation(
                corporation,
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "updatedAt"))
        ).getContent();
    }

}
