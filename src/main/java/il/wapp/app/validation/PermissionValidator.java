package il.wapp.app.validation;

import il.wapp.app.domain.*;
import il.wapp.app.exception.*;
import il.wapp.app.repository.*;
import il.wapp.app.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.*;

@Service
public class PermissionValidator extends BaseEntityValidator {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactListRepository contactListRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private WhatsAppTemplateRepository whatsAppTemplateRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public ContactList validateContactList(Long contactListId) {

        Optional<ContactList> contactList = contactListRepository.findById(contactListId);
        if (!contactList.isPresent()) {
            throw new AttrValidationErrorException(String.format("Not found ContactList with id = %s", contactListId));
        }
        User currentUser = userService.currentUser();
        Corporation corporation = contactList.get().getUser().getCorporation();
        if (!corporation.equals(currentUser.getCorporation())) {
            throw new PermissionException(
                    String.format("Permission denied for contactList = %s and corporation with = %s",
                            contactList.get().getName(), currentUser.getCorporation().getName()
                    ));
        }
        return contactList.get();

    }

    @Transactional
    public Campaign validateCampaign(Long campaignId) {

        Optional<Campaign> campaign = campaignRepository.findById(campaignId);
        if (!campaign.isPresent()) {
            throw new AttrValidationErrorException(String.format("Not found campaign with id = %s", campaignId));
        }
        User currentUser = userService.currentUser();
        Corporation corporation = campaign.get().getUser().getCorporation();
        if (!corporation.equals(currentUser.getCorporation())) {
            throw new PermissionException(
                    String.format("Permission denied for contactList = %s and corporation with = %s",
                            campaign.get().getName(), currentUser.getCorporation().getName()
                    ));
        }
        return campaign.get();

    }

    @Transactional
    public WhatsAppTemplate validateWhatsAppTemplate(Long id) {

        Optional<WhatsAppTemplate> whatsAppTemplate = whatsAppTemplateRepository.findById(id);
        if (!whatsAppTemplate.isPresent()) {
            throw new AttrValidationErrorException(String.format("Not found whatsAppTemplate with id = %s", id));
        }
        User currentUser = userService.currentUser();
        Corporation corporation = whatsAppTemplate.get().getCorporation();
        if (!corporation.equals(currentUser.getCorporation())) {
            throw new PermissionException(
                    String.format("Permission denied for contactList = %s and corporation with = %s",
                            whatsAppTemplate.get().getName(), currentUser.getCorporation().getName()
                    ));
        }
        return whatsAppTemplate.get();

    }

    @Transactional
    public Contact validateContact(Long contactListId, Long contactId) {

        Optional<Contact> contact = contactRepository.findById(contactId);
        if (!contact.isPresent()) {
            throw new AttrValidationErrorException(String.format("Not found Contact with id = %s", contactId));
        }
        User currentUser = userService.currentUser();
        Corporation corporation = contact.get().getUser().getCorporation();
        if (!corporation.equals(currentUser.getCorporation())) {
            throw new PermissionException(String.format("Permission denied for contactId = %s and corporation = %s",
                    contactId, corporation.getName()
            ));
        }
        return contact.get();

    }


}
