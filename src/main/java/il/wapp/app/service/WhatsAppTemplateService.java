package il.wapp.app.service;

import il.wapp.app.dto.campaign.WhatsAppMessageTemplateDto;
import il.wapp.app.dto.common.SearchDto;
import il.wapp.app.dto.whatsapp.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface WhatsAppTemplateService {

	Long save(WhatsAppTemplateDto dto);

	Long edit(TemplateDto dto);

	void delete(Long id);

//	NexmoTemplateApproveDto sendForApproval(Long id);

	Page<TemplateDto> findAll(Long templateId, Pageable pageable);

	TemplateDto getById(Long templateId);

	List<SearchDto> findByNameLike(String name, int limit);
}
