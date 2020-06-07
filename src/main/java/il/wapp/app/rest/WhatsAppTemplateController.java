package il.wapp.app.rest;

import il.wapp.app.dto.campaign.WhatsAppMessageTemplateDto;
import il.wapp.app.dto.common.SearchDto;
import il.wapp.app.dto.whatsapp.NexmoTemplateApproveDto;
import il.wapp.app.dto.whatsapp.TemplateDto;
import il.wapp.app.dto.whatsapp.WhatsAppTemplateDto;
import il.wapp.app.security.RealUser;
import il.wapp.app.service.WhatsAppTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/templates")
public class WhatsAppTemplateController {

    @Autowired
    private WhatsAppTemplateService whatsAppTemplateService;

    @RequestMapping(method = RequestMethod.POST)
    @RealUser
    public Long save(@RequestBody WhatsAppTemplateDto dto) {
        return whatsAppTemplateService.save(dto);
    }

//	@RequestMapping(value = "/send/{templateId}",  method = RequestMethod.POST)
//	@RealUser
//	public NexmoTemplateApproveDto send(@PathVariable Long templateId) {
//		return whatsAppTemplateService.sendForApproval(templateId);
//	}

    @RequestMapping(method = RequestMethod.GET)
    @RealUser
    public Page<TemplateDto> findAll(
            @RequestParam(value = "templateId", required = false) Long templateId,
            @RequestParam(value = "page", defaultValue = "0") int pageIndex,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "direction", defaultValue = "asc", required = false) String direction
    ) {
        return whatsAppTemplateService.findAll(templateId, PageRequest.of(pageIndex, pageSize,
                Sort.by("asc".equals(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sort)));
    }

    @RequestMapping(value = "/{templateId}", method = RequestMethod.GET)
    @RealUser
    public TemplateDto getById(@PathVariable("templateId") Long templateId) {
        return whatsAppTemplateService.getById(templateId);
    }

    @GetMapping("/search")
    @RealUser
    public List<SearchDto> findByNameLike(
            @RequestParam(value = "search", defaultValue = "") String name,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return whatsAppTemplateService.findByNameLike(name, limit);
    }

}
