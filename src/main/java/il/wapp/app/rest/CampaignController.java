package il.wapp.app.rest;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import il.wapp.app.dto.campaign.*;
import il.wapp.app.dto.common.SearchDto;
import il.wapp.app.dto.user.UserDto;
import il.wapp.app.security.*;
import il.wapp.app.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(method = RequestMethod.POST)
    @RealUser
    public Long save(@RequestBody CampaignMessageDto campaignMessage) {
        return campaignService.save(campaignMessage);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @RealUser
    public Long edit(@RequestBody CampaignMessageDto campaign) {
        return campaignService.edit(campaign);
    }

    @RequestMapping(value = "/{campaignId}", method = RequestMethod.DELETE)
    @RealUser
    public void delete(@PathVariable("campaignId") Long campaignId) {
        campaignService.delete(campaignId);
    }

    @RequestMapping(value = "/send/{campaignMessageId}", method = RequestMethod.POST)
    @RealUser
    public SendCampaignMessageDto send(@PathVariable Long campaignMessageId) {
        return campaignService.sendCampaignMessage(campaignMessageId);
    }

    @RequestMapping(method = RequestMethod.GET)
    @RealUser
    public Page<CampaignDto> findAll(
            @RequestParam(value = "campaignId", required = false) Long campaignId,
            @RequestParam(value = "page", defaultValue = "0") int pageIndex,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "direction", defaultValue = "asc", required = false) String direction
    ) {
        return campaignService.findAll(campaignId, PageRequest.of(pageIndex, pageSize,
                Sort.by("asc".equals(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sort)));
    }

    @PostMapping(path = "/upload")
    @RealUser
    public String uploadFile(@RequestPart(value = "file") MultipartFile files) throws Exception {
        return minioService.uploadCampaignFile(files.getOriginalFilename(), files.getBytes());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RealUser
    public CampaignMessageDto getById(@PathVariable("id") Long id) {
        return campaignService.findById(id).toCampaignMessageDto(objectMapper);
    }

    @GetMapping("/search")
    @RealUser
    public List<SearchDto> findByNameLike(
            @RequestParam(value = "search", defaultValue = "") String name,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return campaignService.findByNameLike(name, limit);
    }

}
