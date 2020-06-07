package il.wapp.app.dto.campaign;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SendCampaignMessageDto implements Serializable {

	private String templateName;

	private Long campaignId;

	private SmsMessageDto sms;

	private CampaignMessageDto.WhatsAppReplayMessageDto whatsAppReplayMessage;

	private SendWhatsAppMessageTemplateDto whatsAppTemplateMessage;

	@Data
	@Builder
	public static class SmsMessageDto {
		private boolean notReceiveWatsApp;
		private boolean resendTomorrow;
		private boolean nothingAddToSms;
		private String additionalMessage;
		private String message;
	}
}
