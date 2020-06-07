package il.wapp.app.service.impl;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import il.wapp.app.domain.*;
import il.wapp.app.dto.campaign.*;
import il.wapp.app.dto.common.SearchDto;
import il.wapp.app.dto.whatsapp.WhatsAppTemplateDto;
import il.wapp.app.enums.*;
import il.wapp.app.exception.*;
import il.wapp.app.kafka.*;
import il.wapp.app.repository.*;
import il.wapp.app.service.*;
import il.wapp.app.validation.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.kafka.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.client.*;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.*;

@Service
@Slf4j
public class CampaignServiceImpl implements CampaignService {

  @Autowired
  private CampaignRepository campaignRepository;

  @Autowired
  private WhatsAppTemplateRepository whatsAppTemplateRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PermissionValidator permissionValidator;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${whasapp.link.add.contact}")
  private String whatsAppAddLink;

  @Value("${short.link.service.url}")
  private String shortLinkUrl;

  @Value("${short.link.service.url.public}")
  private String shortLinkUrlPublic;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private Producer producer;

  private ContactList validate(CampaignMessageDto campaignMessage) {
    ContactList contactList = null;
    if (campaignMessage.getContactListId() != null && campaignMessage.getContactListId() != 0) {
      contactList = permissionValidator.validateContactList(campaignMessage.getContactListId());
      List<Contact> contacts = contactList.getContacts();
      if (contacts == null || contacts.size() == 0) {
        throw new CampaignException(
            String.format("No contacts for contactListId = %s", contactList.getId()));
      }

    }

    if (campaignMessage.getWhatsAppTemplateMessage() != null
        && campaignMessage.getWhatsAppTemplateMessage().getTemplateId() != null) {
      Optional<WhatsAppTemplate> whatsAppTemplateOptional = whatsAppTemplateRepository
          .findById(campaignMessage.getWhatsAppTemplateMessage().getTemplateId());
      if (!whatsAppTemplateOptional.isPresent()) {
        throw new CampaignException("WhatsAppTemplate is empty");
      }
    }
    return contactList;
  }

  @Override
  @Transactional
  public Long save(CampaignMessageDto campaignMessage) {
    ContactList contactList = validate(campaignMessage);
    Campaign campaign = new Campaign();
    campaign.setCreatedAt(new Date());
    campaign.setUser(userService.currentUser());
    campaign.setContactList(contactList);
    try {
      campaign.setJsonContent(objectMapper.writeValueAsString(campaignMessage));
    } catch (JsonProcessingException ex) {
      throw new ParseJsonException(ex.getMessage(), ex);
    }
    campaign.setStatus(CampaignStatus.OPENED);
    campaign.setName(campaignMessage.getName());
    campaignRepository.save(campaign);
    return campaign.getId();
  }


  @Override
  @Transactional
  public Long edit(CampaignMessageDto campaignMessage) {
    Campaign campaign = permissionValidator.validateCampaign(campaignMessage.getCampaignId());
    campaign.setName(campaignMessage.getName());
    ContactList contactList = validate(campaignMessage);
    campaign.setUser(userService.currentUser());
    campaign.setContactList(contactList);
    try {
      campaign.setJsonContent(objectMapper.writeValueAsString(campaignMessage));
    } catch (JsonProcessingException ex) {
      throw new ParseJsonException(ex.getMessage(), ex);
    }
    campaign.setName(campaignMessage.getName());
    return campaignRepository.save(campaign).getId();
  }

  @Override
  @Transactional
  public void delete(Long id) {
    campaignRepository.delete(permissionValidator.validateCampaign(id));
  }

  private Campaign checkCampaignStatus(Long campaignMessageId) {
    Campaign campaign = permissionValidator.validateCampaign(campaignMessageId);
    if (CampaignStatus.RUNNING.equals(campaign.getStatus())) {
      throw new CampaignException("Campaign is running");
    } else if (CampaignStatus.CLOSED.equals(campaign.getStatus())) {
      throw new CampaignException("Campaign already executed");
    }
    Date current = new Date();
    campaign.setSendingAt(current);
    campaign.setStatus(CampaignStatus.RUNNING);
    campaignRepository.save(campaign);
    return campaign;
  }

  @Override
  @Transactional
  public synchronized SendCampaignMessageDto sendCampaignMessage(Long campaignMessageId) {
    Campaign campaign = checkCampaignStatus(campaignMessageId);
    try {
      CampaignMessageDto campaignMessage = objectMapper
          .readValue(campaign.getJsonContent(), CampaignMessageDto.class);
      WhatsAppAccount whatsAppAccount =
          userService.currentUser().getCorporation().getWhatsAppAccount();
      if (whatsAppAccount == null || whatsAppAccount.getAppNumber() == null) {
        throw new CampaignException("whatsAppAccount is empty");
      }
      campaignMessage.setCampaignId(campaign.getId());
      campaignMessage.setWhasappLinkAddContact(whatsAppAddLink);
      campaignMessage.setWhasappNumber(whatsAppAccount.getAppNumber());

      SendCampaignMessageDto sendCampaignMessage = campaignMessage.toSendDto();
      checkAdditionalLink(campaignMessage, sendCampaignMessage);
      Long templateId = campaignMessage.getWhatsAppTemplateMessage().getTemplateId();
      if (templateId != null) {
        Optional<WhatsAppTemplate> whatsAppTemplateOptional = whatsAppTemplateRepository
            .findById(campaignMessage.getWhatsAppTemplateMessage().getTemplateId());
        if (!whatsAppTemplateOptional.isPresent()) {
          throw new CampaignException("WhatsAppTemplate is empty");
        }

        sendCampaignMessage.setTemplateName(whatsAppTemplateOptional.get().getName());
        WhatsAppTemplateDto messageTemplateDto = objectMapper
            .readValue(whatsAppTemplateOptional.get().getJsonContent(), WhatsAppTemplateDto.class);
        WhatsAppMessageTemplateDto whatsAppMessageTemplateDto
            = campaignMessage.getWhatsAppTemplateMessage();

        sendCampaignMessage.setWhatsAppTemplateMessage(new SendWhatsAppMessageTemplateDto());
        sendCampaignMessage.getWhatsAppTemplateMessage()
            .setMessageTemplate(MessageTemplateDto.builder()
                .from(BaseMessageTemplateDto.TypeNumber.builder()
                    .number(whatsAppAccount.getAppNumber())
                    .type("whatsapp")
                    .build())
                .to(BaseMessageTemplateDto.TypeNumber.builder()
                    .number("'$TO_NUMBER'")
                    .type("whatsapp")
                    .build())
                .message(BaseMessageTemplateDto.MessageTemplate.builder()
                    .content(BaseMessageTemplateDto.Content.builder()
                        .type("custom")
                        .custom(BaseMessageTemplateDto.Custom.builder()
                            .type("template")
                            .template(BaseMessageTemplateDto.Template.builder()
                                .name(whatsAppTemplateOptional.get().getName())
                                .namespace(whatsAppAccount.getAppTemplateNamespace())
                                .language(BaseMessageTemplateDto.Language.builder()
                                    .code(messageTemplateDto.getLanguage().getCode())
                                    .policy("deterministic")
                                    .build())
                                .components(
                                    checkComponents(whatsAppMessageTemplateDto.getComponents()))
                                .build())
                            .build())
                        .build())
                    .build())
                .build());

      }
      log.info("sendCampaignMessage = "
          + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sendCampaignMessage));

      producer.sendCampaign(sendCampaignMessage);
      campaign.setSendingAt(new Date());
      campaignRepository.save(campaign);
      return sendCampaignMessage;
    } catch (JsonProcessingException ex) {
      throw new ParseJsonException(ex.getMessage(), ex);
    }

  }

  private List<BaseMessageTemplateDto.Component> checkComponents(
      List<BaseMessageTemplateDto.Component> components) {
    return components.stream().filter(component -> !component.getParameters().isEmpty())
        .collect(Collectors.toList());
  }

  private void checkAdditionalLink(CampaignMessageDto campaignMessage,
      SendCampaignMessageDto sendCampaignMessageDto) {
    if (campaignMessage.getSms() != null) {
      CampaignMessageDto.AdditionalMessage additionalMessage = campaignMessage.getSms()
          .getAdditionalLink();
      if (additionalMessage != null && additionalMessage.isReplaceLinkToUnique()
          && true == additionalMessage.isSelected()) {
        try {
          String shortLink =
              restTemplate.postForObject(shortLinkUrl + additionalMessage.getContent(),
                  new HttpEntity(null),
                  String.class);
          sendCampaignMessageDto.getSms().setAdditionalMessage(shortLinkUrlPublic + shortLink);
        } catch (Exception ex) {
          log.error(ex.getMessage(), ex);
        }

      }
    }
  }

  private void sendMessagesForContact(Contact contact, Long campaignId, String whatsAppNumber) {
    producer.sendWhatsAppMessage(CampaignContactDto.builder()
        .campaignId(campaignId)
        .name(contact.getName())
        .from(whatsAppNumber)
        .to(contact.getPhone().getNumber())
        .build());
  }

  @Override
  public Page<CampaignDto> findAll(Long campaignId, Pageable pageable) {
    List<Campaign> campaigns;
    if (campaignId != null) {
      campaigns = Arrays.asList(permissionValidator.validateCampaign(campaignId));
    } else {
      campaigns = campaignRepository
          .findAllByCorporation(userService.currentUser().getCorporation(), pageable).getContent();
    }
    return new PageImpl(campaigns
        .stream().map(c -> c.toDto()).collect(Collectors.toList()), pageable, campaigns.size());
  }

  @KafkaListener(topics = "${kafka.topic.nexmo_prepare_template_status_uri}",
      containerFactory = "kafkaListenerContainerFactory")
  @Transactional
  public void listen(HashMap message) throws Exception {
    log.info(String.format("campaignMessageStatus" + " -> Listener message -> %s", message));
    try {
      if (message == null || message.get("campaignId") == null || message.get("ready") == null) {
        return;
      }
      log.info("1");
      Integer campaignMessageId = (Integer) message.get("campaignId");
      boolean ready = (boolean) message.get("ready");
      log.info("2");
      if (ready) {
        log.info("3");
        Optional<Campaign> optionalCampaign = campaignRepository
            .findById(campaignMessageId.longValue());
        log.info("4");
        if (optionalCampaign.isPresent()) {
          log.info("5");
          Campaign campaign = optionalCampaign.get();
          String whatsAppNumber = campaign.getContactList().getUser().getCorporation()
              .getWhatsAppAccount()
              .getAppNumber();
          log.info("campaign.getContactList().getContacts()" + campaign.getContactList().getContacts().size());
          campaign.getContactList().getContacts().stream().forEach(contact ->{
            log.info("phoneNumber = "+contact.getPhone().getNumber());
            sendMessagesForContact(contact, campaign.getId(), whatsAppNumber);
              }

          );
          log.info("7");
          campaign.setStatus(CampaignStatus.CLOSED);
          campaignRepository.save(campaign);
        } else {
          log.info("Campaign not found");
        }
      }
    } catch (Exception ex) {
      log.info(ex.getMessage());
    }
  }

  @Override
  public Campaign findById(Long id) {
    return permissionValidator.validateCampaign(id);
  }

  @Override
  public List<SearchDto> findByNameLike(String name, int limit) {
    List<Object[]> result = entityManager.createQuery("select c.id, c.name from Campaign c " +
        "where UPPER(c.name) like UPPER(:name) and c.user.corporation=:corporation order by c.name")
        .setParameter("name", "%" + name + "%")
        .setParameter("corporation", userService.currentUser().getCorporation())
        .setMaxResults(limit)
        .getResultList();
    return result.stream()
        .map(object -> new SearchDto((Long) object[0], (String) object[1]))
        .collect(Collectors.toList());
  }
}
