package il.wapp.app.dto.campaign;

import com.fasterxml.jackson.annotation.*;
import il.wapp.app.dto.whatsapp.WhatsAppTemplateDto;
import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MessageTemplateDto {

    private BaseMessageTemplateDto.TypeNumber from;
    private BaseMessageTemplateDto.TypeNumber to;
    private BaseMessageTemplateDto.MessageTemplate message;



}
