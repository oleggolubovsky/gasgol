package il.wapp.app.service;

import il.wapp.app.domain.Campaign;
import il.wapp.app.dto.campaign.*;
import il.wapp.app.dto.common.SearchDto;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface CampaignService {

	Long save(CampaignMessageDto campaignMessage);

	Long edit(CampaignMessageDto dto);

	void delete(Long id);

	SendCampaignMessageDto sendCampaignMessage(Long campaignMessageId);

	Page<CampaignDto> findAll(Long campaignId, Pageable pageable);

	Campaign findById(Long id);

	List<SearchDto> findByNameLike(String name, int limit);

}
