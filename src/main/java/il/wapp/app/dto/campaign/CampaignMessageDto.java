package il.wapp.app.dto.campaign;

import lombok.*;

import java.io.*;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CampaignMessageDto implements Serializable {

	private String name;

	private Long campaignId;

	private Long contactListId;

	private String whasappLinkAddContact;

	private String whasappNumber;

	private String status;

	private SmsMessageDto sms;

	private WhatsAppReplayMessageDto whatsAppReplayMessage;

	private WhatsAppMessageTemplateDto whatsAppTemplateMessage;

	@Data
	static class WhatsAppReplayMessageDto {
		private List<String> mediaFiles;
		private String message;
		private boolean rtl;
	}

	@Data
	public static class SmsMessageDto {
		private boolean notReceiveWatsApp;
		private boolean resendTomorrow;
		private boolean nothingAddToSms;
		private AdditionalMessage additionalFW;
		private AdditionalMessage additionalLink;
		private String message;
		private boolean rtl;
	}

	@Data
	public static class AdditionalMessage {
		private String content;
		private boolean replaceLinkToUnique;
		private boolean selected;
	}

	public SendCampaignMessageDto toSendDto() {
		SendCampaignMessageDto sendCampaignMessage = SendCampaignMessageDto.builder()
				.campaignId(campaignId)
//				.whatsAppReplayMessage(whatsAppReplayMessage)
				.sms(generateSmsMessage(sms))
//				.whatsAppTemplateMessage(whatsAppTemplateMessage)
				.build();
		if (whatsAppReplayMessage != null && (!StringUtils.isBlank(whatsAppReplayMessage.getMessage())
				|| (whatsAppReplayMessage.getMediaFiles() != null && !whatsAppReplayMessage.getMediaFiles()
				.isEmpty()))) {
			sendCampaignMessage.setWhatsAppReplayMessage(whatsAppReplayMessage);
		}
		return sendCampaignMessage;
	}

	public SendCampaignMessageDto.SmsMessageDto generateSmsMessage(SmsMessageDto sms) {
		if (sms.getMessage() == null || sms.getMessage().isEmpty()){
			return null;
		}
		SendCampaignMessageDto.SmsMessageDto smsSendMessage = SendCampaignMessageDto.SmsMessageDto.builder()
			.message(sms.getMessage())
			.nothingAddToSms(sms.isNothingAddToSms())
			.notReceiveWatsApp(sms.isNotReceiveWatsApp())
			.resendTomorrow(sms.isResendTomorrow())
			.build();
		if (sms.isNothingAddToSms()) {
			return smsSendMessage;
		}
		AdditionalMessage additionalMessage = sms.getAdditionalFW();
		if (additionalMessage != null && true == additionalMessage.isSelected()) {
			String content = additionalMessage.getContent();
			smsSendMessage.setAdditionalMessage(String.format(whasappLinkAddContact,
					whasappNumber, get(0, content) + "%20", get(1, content) + "%20", get(2, content)));
			return smsSendMessage;
		}
		return smsSendMessage;
	}

	private static String get(int i, String str) {
		try {
			return str.split(" ")[i];
		} catch (IndexOutOfBoundsException ex) {
			return "";
		}
	}

}
