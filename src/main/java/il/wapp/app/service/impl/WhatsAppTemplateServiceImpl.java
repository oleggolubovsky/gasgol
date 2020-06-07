package il.wapp.app.service.impl;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import il.wapp.app.domain.*;
import il.wapp.app.dto.campaign.WhatsAppMessageTemplateDto;
import il.wapp.app.dto.common.SearchDto;
import il.wapp.app.dto.whatsapp.*;
import il.wapp.app.enums.*;
import il.wapp.app.exception.*;
import il.wapp.app.kafka.*;
import il.wapp.app.repository.*;
import il.wapp.app.service.*;
import il.wapp.app.validation.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.*;

@Service
@Slf4j
public class WhatsAppTemplateServiceImpl implements WhatsAppTemplateService {

    @Autowired
    private WhatsAppTemplateRepository whatsAppTemplateRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PermissionValidator permissionValidator;

    @Value("${whasapp.link.add.contact}")
    private String whatsAppAddLink;

    @Autowired
    private Producer producer;

    @Override
    @Transactional
    public Long save(WhatsAppTemplateDto dto) {
//		Todo validation
        WhatsAppTemplate whatsAppTemplate = new WhatsAppTemplate();
        User user = userService.currentUser();
        whatsAppTemplate.setCreatedAt(new Date());
        whatsAppTemplate.setCorporation(user.getCorporation());
//        if (dto.getMessage() != null && dto.getMessage().getContent() != null
//                && dto.getMessage().getContent().getCustom() != null
//                && dto.getMessage().getContent().getCustom().getTemplate() != null) {
//            WhatsAppMessageTemplateDto.Template template = dto.getMessage().getContent().getCustom().getTemplate();
//            whatsAppTemplate.setName(template.getName());
//            WhatsAppAccount whatsAppAccount = user.getCorporation().getWhatsAppAccount();
//            if (whatsAppAccount != null) {
//                template.setNamespace(whatsAppAccount.getAppTemplateNamespace());
//                dto.setFrom(WhatsAppMessageTemplateDto.TypeNumber.builder()
//                        .number(whatsAppAccount.getAppNumber()).build());
//            }
//        } else {
//            throw new ParseJsonException("Template incorrect defined");
//        }

        whatsAppTemplate.setStatus(WhatsAppTemplateStatus.NEW);
        whatsAppTemplateRepository.save(whatsAppTemplate);
        try {
            whatsAppTemplate.setJsonContent(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException ex) {
            throw new ParseJsonException(ex.getMessage(), ex);
        }
        return whatsAppTemplate.getId();
    }

    @Override
    @Transactional
    public Long edit(TemplateDto dto) {
        WhatsAppTemplate whatsAppTemplate = permissionValidator.validateWhatsAppTemplate(dto.getId());
        whatsAppTemplate.setName(dto.getName());
        return whatsAppTemplateRepository.save(whatsAppTemplate).getId();
    }

    @Override
    @Transactional
    public TemplateDto getById(Long templateId) {
        WhatsAppTemplate whatsAppTemplate = permissionValidator.validateWhatsAppTemplate(templateId);
        return whatsAppTemplate.toDto(objectMapper);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        whatsAppTemplateRepository.delete(permissionValidator.validateWhatsAppTemplate(id));
    }

//    @Override
//    @Transactional
//    public NexmoTemplateApproveDto sendForApproval(Long id) {
//        WhatsAppTemplate whatsAppTemplate = permissionValidator.validateWhatsAppTemplate(id);
//        try {
//            WhatsAppTemplateDto whatsAppTemplateDto = objectMapper
//                    .readValue(whatsAppTemplate.getJsonContent(), WhatsAppTemplateDto.class);
//            WhatsAppAccount whatsAppAccount =
//                    userService.currentUser().getCorporation().getWhatsAppAccount();
//            if (whatsAppAccount == null || whatsAppAccount.getAppNumber() == null) {
//                throw new CampaignException("whatsAppAccount is empty");
//            }
//            whatsAppTemplateDto.setTemplateId(whatsAppTemplate.getId());
//            whatsAppTemplateDto.setWhatsAppNumber(whatsAppAccount.getAppNumber());
//            whatsAppTemplateDto.setAppTemplateNamespace(whatsAppAccount.getAppTemplateNamespace());
//            //producer.sendCampaign(campaignMessage.toSendDto());
//            whatsAppTemplate.setStatus(WhatsAppTemplateStatus.VERIFICATION);
//            return whatsAppTemplateDto.toNexmoTemplate();
//        } catch (JsonProcessingException ex) {
//            throw new ParseJsonException(ex.getMessage(), ex);
//        }
//    }

    @Override
    public Page<TemplateDto> findAll(Long templateId, Pageable pageable) {
        List<WhatsAppTemplate> templates;
        if (templateId != null) {
            templates = Arrays.asList(permissionValidator.validateWhatsAppTemplate(templateId));
        } else {
            templates
                    = whatsAppTemplateRepository
                    .findAllByCorporation(userService.currentUser().getCorporation(), pageable).getContent();
        }
        return new PageImpl<>(templates
                .stream().map(c -> c.toDto()).collect(Collectors.toList()), pageable, templates.size());
    }

    @Override
    public List<SearchDto> findByNameLike(String name, int limit) {
        List<Object[]> result = entityManager.createQuery("select t.id, t.name from WhatsAppTemplate t " +
                "where UPPER(t.name) like UPPER(:name) and t.corporation=:corporation order by t.name")
                .setParameter("name", "%" + name + "%")
                .setParameter("corporation", userService.currentUser().getCorporation())
                .setMaxResults(limit)
                .getResultList();
        return result.stream()
                .map(object -> new SearchDto((Long) object[0], (String) object[1]))
                .collect(Collectors.toList());
    }

}
