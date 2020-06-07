package il.wapp.app.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import il.wapp.app.dto.campaign.*;
import il.wapp.app.enums.*;
import il.wapp.app.exception.ParseJsonException;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.*;
import java.util.*;

@Data
@Entity
@Table(name = "campaigns")
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
public class Campaign implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(name = "status")
    private CampaignStatus status;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "sent_at")
    private Date sendingAt;

    @Type(type = "json")
    @Column(name = "json_content", columnDefinition = "json")
    private String jsonContent;

    @ManyToOne
    @JoinColumn(name = "wa_template_id")
    private WhatsAppTemplate whatsAppTemplate;

    @ManyToOne
    @JoinColumn(name = "contact_list_id")
    private ContactList contactList;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public CampaignDto toDto() {
        return CampaignDto.builder()
                .id(id)
                .name(name)
                .createdAt(createdAt)
                .sendingAt(sendingAt)
                .build();
    }

    public CampaignMessageDto toCampaignMessageDto(ObjectMapper objectMapper) {
        try {
            CampaignMessageDto dto = objectMapper
                    .readValue(jsonContent, CampaignMessageDto.class);
            dto.setCampaignId(id);
            dto.setName(name);
            dto.setStatus(status.name());
            return dto;
        } catch (Exception ex) {
            throw new ParseJsonException("Error parsing for campaign id = " + id);
        }
    }

}
