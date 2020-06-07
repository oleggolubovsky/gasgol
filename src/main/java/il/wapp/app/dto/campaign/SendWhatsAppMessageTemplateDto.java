package il.wapp.app.dto.campaign;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SendWhatsAppMessageTemplateDto extends BaseMessageTemplateDto{

	private MessageTemplateDto messageTemplate;

}
