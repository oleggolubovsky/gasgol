package il.wapp.app.dto.campaign;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class WhatsAppMessageTemplateDto extends BaseMessageTemplateDto{

	private List<Component> components;

}
