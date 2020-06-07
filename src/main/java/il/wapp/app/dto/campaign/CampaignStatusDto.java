package il.wapp.app.dto.campaign;

import lombok.*;

import java.io.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignStatusDto implements Serializable {

	private boolean ready;

	private Long campaignId;

	private String errorMessage;

}
